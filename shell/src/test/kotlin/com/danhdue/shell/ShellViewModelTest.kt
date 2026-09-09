/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.shell

import app.cash.turbine.test
import com.danhdue.libraries.testutils.TestCoroutineRule
import com.danhdue.platform.AppEvent
import com.danhdue.platform.AppEventBus
import com.danhdue.platform.FeatureInstaller
import com.danhdue.platform.NoOpFeatureInstaller
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Task 11 pilot (subscribe side): [ShellViewModel] collects
 * [AppEvent.ProfileNameChanged] from the [AppEventBus] and mirrors the name into
 * [ShellState.profileName] — with no import from `com.danhdue.settings.*`.
 *
 * Task 14: selecting the Scanner tab drives [FeatureInstaller.ensureInstalled]
 * for the on-demand `scanner` split — see the `scanner install branch` tests,
 * which use a [FakeFeatureInstaller] whose completion the test controls (unlike
 * [NoOpFeatureInstaller], which resolves synchronously).
 */
class ShellViewModelTest {
    @get:Rule
    val coroutineRule = TestCoroutineRule()

    /** A [FeatureInstaller] whose `ensureInstalled` suspends until [release] (or fails). */
    private class FakeFeatureInstaller : FeatureInstaller {
        val requestedModules = mutableListOf<String>()
        var failure: Throwable? = null
        private var gate: CompletableDeferred<Unit>? = null

        override suspend fun ensureInstalled(
            module: String,
            onReady: () -> Unit,
        ) {
            requestedModules += module
            val g = CompletableDeferred<Unit>()
            gate = g
            g.await()
            failure?.let { throw it }
            onReady()
        }

        fun release() {
            gate?.complete(Unit)
        }
    }

    @Test
    fun `mirrors ProfileNameChanged into shell state`() =
        coroutineRule.runTest {
            val bus = AppEventBus()
            val viewModel = ShellViewModel(bus, NoOpFeatureInstaller())

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
            val viewModel = ShellViewModel(bus, NoOpFeatureInstaller())

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
            val viewModel = ShellViewModel(bus, NoOpFeatureInstaller())

            bus.on<AppEvent>().test {
                // The shell opens on Settings (the last tab); selecting Home is a real change.
                assertEquals(ShellTab.Settings, viewModel.uiState.value.selectedTab)

                viewModel.dispatch(ShellAction.TabSelected(ShellTab.Home))

                assertEquals(ShellTab.Home, viewModel.uiState.value.selectedTab)
                expectNoEvents()
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `scanner install branch - first Scanner tab tap kicks off install and toggles state`() =
        coroutineRule.runTest {
            val installer = FakeFeatureInstaller()
            val viewModel = ShellViewModel(AppEventBus(), installer)

            viewModel.dispatch(ShellAction.TabSelected(ShellTab.Scanner))

            assertEquals(listOf("scanner"), installer.requestedModules)
            assertTrue("installing while the split downloads", "scanner" in viewModel.uiState.value.installingModules)
            assertFalse("scanner" in viewModel.uiState.value.readyModules)

            installer.release()

            assertTrue("ready once the split is installed", "scanner" in viewModel.uiState.value.readyModules)
            assertFalse("scanner" in viewModel.uiState.value.installingModules)
        }

    @Test
    fun `scanner install branch - rapid re-tap does not start a second install`() =
        coroutineRule.runTest {
            val installer = FakeFeatureInstaller()
            val viewModel = ShellViewModel(AppEventBus(), installer)

            viewModel.dispatch(ShellAction.TabSelected(ShellTab.Scanner))
            viewModel.dispatch(ShellAction.TabSelected(ShellTab.Scanner))
            viewModel.dispatch(ShellAction.TabSelected(ShellTab.Scanner))

            assertEquals(listOf("scanner"), installer.requestedModules)
        }

    @Test
    fun `scanner install branch - install failure clears the flag so the tab is re-tappable`() =
        coroutineRule.runTest {
            val installer = FakeFeatureInstaller()
            val viewModel = ShellViewModel(AppEventBus(), installer)

            installer.failure = IllegalStateException("split download failed")
            viewModel.dispatch(ShellAction.TabSelected(ShellTab.Scanner))
            installer.release()

            assertFalse("scanner" in viewModel.uiState.value.installingModules)
            assertFalse("scanner" in viewModel.uiState.value.readyModules)

            installer.failure = null
            viewModel.dispatch(ShellAction.TabSelected(ShellTab.Scanner))

            assertEquals(listOf("scanner", "scanner"), installer.requestedModules)

            installer.release()
            assertTrue("scanner" in viewModel.uiState.value.readyModules)
        }
}
