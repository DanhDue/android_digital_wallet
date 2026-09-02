/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.shell

import app.cash.turbine.test
import com.danhdue.libraries.testutils.TestCoroutineRule
import com.danhdue.platform.AppEvent
import com.danhdue.platform.AppEventBus
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Task 11 pilot (subscribe side): [ShellViewModel] collects
 * [AppEvent.ProfileNameChanged] from the [AppEventBus] and mirrors the name into
 * [ShellState.profileName] — with no import from `com.danhdue.settings.*`.
 */
class ShellViewModelTest {
    @get:Rule
    val coroutineRule = TestCoroutineRule()

    @Test
    fun `mirrors ProfileNameChanged into shell state`() =
        coroutineRule.runTest {
            val bus = AppEventBus()
            val viewModel = ShellViewModel(bus)

            viewModel.uiState.test {
                assertEquals("", awaitItem().profileName)

                bus.publish(AppEvent.ProfileNameChanged(displayName = "Ada Lovelace"))

                assertEquals("Ada Lovelace", awaitItem().profileName)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `ignores unrelated events`() =
        coroutineRule.runTest {
            val bus = AppEventBus()
            val viewModel = ShellViewModel(bus)

            viewModel.uiState.test {
                assertEquals("", awaitItem().profileName)

                bus.publish(AppEvent.UserLoggedOut)

                expectNoEvents()
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `tab selection still updates state without touching the bus`() =
        coroutineRule.runTest {
            val bus = AppEventBus()
            val viewModel = ShellViewModel(bus)

            bus.on<AppEvent>().test {
                viewModel.dispatch(ShellAction.TabSelected(ShellTab.Settings))

                assertEquals(ShellTab.Settings, viewModel.uiState.value.selectedTab)
                expectNoEvents()
                cancelAndConsumeRemainingEvents()
            }
        }
}
