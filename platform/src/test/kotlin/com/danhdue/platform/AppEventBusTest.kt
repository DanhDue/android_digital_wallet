/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform

import app.cash.turbine.test
import app.cash.turbine.testIn
import app.cash.turbine.turbineScope
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Behaviour of [AppEventBus] — the typed cross-feature broadcast bus.
 *
 * An [UnconfinedTestDispatcher] is used so Turbine's collectors are subscribed
 * eagerly (before the first `publish`), matching the "no replay" contract.
 */
class AppEventBusTest {
    @Test
    fun `on emits only events matching the requested type`() =
        runTest(UnconfinedTestDispatcher()) {
            val bus = AppEventBus()

            bus.on<AppEvent.UserLoggedOut>().test {
                bus.publish(AppEvent.ShellTabVisibilityChanged(tabIndex = 2, isVisible = false))
                bus.publish(AppEvent.AppLifecycleChanged(AppLifecycleState.STOPPED))
                bus.publish(AppEvent.UserLoggedOut)

                assertEquals(AppEvent.UserLoggedOut, awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `on delivers the full payload of a matching data-class event`() =
        runTest(UnconfinedTestDispatcher()) {
            val bus = AppEventBus()
            val expected = AppEvent.ShellTabVisibilityChanged(tabIndex = 1, isVisible = true)

            bus.on<AppEvent.ShellTabVisibilityChanged>().test {
                bus.publish(expected)

                assertEquals(expected, awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `every active subscriber receives every published event`() =
        runTest(UnconfinedTestDispatcher()) {
            val bus = AppEventBus()

            turbineScope {
                val first = bus.on<AppEvent.UserLoggedOut>().testIn(this)
                val second = bus.on<AppEvent.UserLoggedOut>().testIn(this)

                bus.publish(AppEvent.UserLoggedOut)

                assertEquals(AppEvent.UserLoggedOut, first.awaitItem())
                assertEquals(AppEvent.UserLoggedOut, second.awaitItem())

                first.cancelAndConsumeRemainingEvents()
                second.cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `publish with no subscriber neither throws nor is replayed to a later subscriber`() =
        runTest(UnconfinedTestDispatcher()) {
            val bus = AppEventBus()

            bus.publish(AppEvent.UserLoggedOut)

            bus.on<AppEvent>().test {
                bus.publish(AppEvent.AppLifecycleChanged(AppLifecycleState.RESUMED))

                assertEquals(AppEvent.AppLifecycleChanged(AppLifecycleState.RESUMED), awaitItem())
                expectNoEvents()
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `rapid publishes are delivered in order`() =
        runTest(UnconfinedTestDispatcher()) {
            val bus = AppEventBus()
            val received = mutableListOf<AppEvent>()
            val collector =
                launch(UnconfinedTestDispatcher(testScheduler)) {
                    bus.on<AppEvent.ShellTabVisibilityChanged>().toList(received)
                }
            val expected =
                (0 until RAPID_PUBLISH_COUNT).map { index ->
                    AppEvent.ShellTabVisibilityChanged(tabIndex = index, isVisible = index % 2 == 0)
                }

            expected.forEach(bus::publish)
            collector.cancel()

            assertEquals(expected, received)
        }

    @Test
    fun `a cancelled collector stops receiving further events`() =
        runTest(UnconfinedTestDispatcher()) {
            val bus = AppEventBus()
            val received = mutableListOf<AppEvent>()
            val collector =
                launch(UnconfinedTestDispatcher(testScheduler)) {
                    bus.events.toList(received)
                }

            bus.publish(AppEvent.UserLoggedOut)
            collector.cancel()
            testScheduler.advanceUntilIdle()
            bus.publish(AppEvent.AppLifecycleChanged(AppLifecycleState.STOPPED))

            assertEquals(listOf(AppEvent.UserLoggedOut), received)
        }

    private companion object {
        const val RAPID_PUBLISH_COUNT = 50
    }
}
