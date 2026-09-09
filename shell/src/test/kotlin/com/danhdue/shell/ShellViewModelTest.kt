/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.shell

import androidx.navigation3.runtime.NavKey
import app.cash.turbine.test
import com.danhdue.framework.navigation.Navigator
import com.danhdue.libraries.testutils.TestCoroutineRule
import com.danhdue.platform.AppEvent
import com.danhdue.platform.AppEventBus
import com.danhdue.platform.AppRoutes
import com.danhdue.platform.FeatureInstaller
import com.danhdue.platform.NoOpFeatureInstaller
import com.danhdue.platform.deeplink.DeepLinkRouter
import com.danhdue.platform.deeplink.FailureReason
import com.danhdue.platform.deeplink.NavigationCommand
import com.danhdue.shell.tabs.HomeStubRoute
import com.danhdue.uikit.R
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

private data object TestProfileRoute : NavKey

private data object TestDetailsRoute : NavKey

private data object TestFullScreenRoute : NavKey

/**
 * Task 11 pilot (subscribe side): [ShellViewModel] collects
 * [AppEvent.ProfileNameChanged] from the [AppEventBus] and mirrors the name into
 * [ShellState.profileName] — with no import from `com.danhdue.settings.*`.
 *
 * Task 14: selecting the Scanner tab drives [FeatureInstaller.ensureInstalled]
 * for the on-demand `scanner` split — see the `scanner install branch` tests,
 * which use a [FakeFeatureInstaller] whose completion the test controls (unlike
 * [NoOpFeatureInstaller], which resolves synchronously).
 *
 * Task 6: [ShellViewModel] consumes [NavigationCommand]s from [DeepLinkRouter] and
 * executes placement commands ([NavigationCommand.OpenInTab],
 * [NavigationCommand.OpenFullScreen], [NavigationCommand.OpenInCurrentTab]).
 */
class ShellViewModelTest {
    @get:Rule
    val coroutineRule = TestCoroutineRule()

    private class FakeDeepLinkRouter : DeepLinkRouter {
        val commandFlow = MutableSharedFlow<NavigationCommand>(extraBufferCapacity = 64)
        override val commands: Flow<NavigationCommand> = commandFlow
        val dispatchedUris = mutableListOf<String>()

        override fun dispatch(uri: String) {
            dispatchedUris += uri
        }

        fun emitCommand(command: NavigationCommand) {
            commandFlow.tryEmit(command)
        }
    }

    private fun createViewModel(
        bus: AppEventBus = AppEventBus(),
        installer: FeatureInstaller = NoOpFeatureInstaller(),
        navigator: Navigator = Navigator(AppRoutes.ShellRoute),
        router: DeepLinkRouter = FakeDeepLinkRouter(),
    ): ShellViewModel = ShellViewModel(bus, installer, navigator, router)

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
            val viewModel = createViewModel(bus = bus)

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
            val viewModel = createViewModel(bus = bus)

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
            val viewModel = createViewModel(bus = bus)

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
            val viewModel = createViewModel(installer = installer)

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
            val viewModel = createViewModel(installer = installer)

            viewModel.dispatch(ShellAction.TabSelected(ShellTab.Scanner))
            viewModel.dispatch(ShellAction.TabSelected(ShellTab.Scanner))
            viewModel.dispatch(ShellAction.TabSelected(ShellTab.Scanner))

            assertEquals(listOf("scanner"), installer.requestedModules)
        }

    @Test
    fun `scanner install branch - install failure clears the flag so the tab is re-tappable`() =
        coroutineRule.runTest {
            val installer = FakeFeatureInstaller()
            val viewModel = createViewModel(installer = installer)

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

    @Test
    fun `OpenInTab selects specified tab and replaces its nested back stack`() =
        coroutineRule.runTest {
            val router = FakeDeepLinkRouter()
            val viewModel = createViewModel(router = router)

            // Initially on Settings tab (2)
            assertEquals(ShellTab.Settings, viewModel.uiState.value.selectedTab)

            val newStack = listOf(HomeStubRoute, TestProfileRoute)
            router.emitCommand(NavigationCommand.OpenInTab(tab = 0, stack = newStack))

            assertEquals(ShellTab.Home, viewModel.uiState.value.selectedTab)
            assertEquals(newStack, viewModel.uiState.value.homeBackStack)
        }

    @Test
    fun `OpenInTab on a tab with deeper stack replaces it rather than appending`() =
        coroutineRule.runTest {
            val router = FakeDeepLinkRouter()
            val viewModel = createViewModel(router = router)

            // Deepen settings back stack first
            viewModel.dispatch(ShellAction.NavigateInTab(ShellTab.Settings, TestProfileRoute))
            viewModel.dispatch(ShellAction.NavigateInTab(ShellTab.Settings, TestDetailsRoute))
            assertEquals(
                3,
                viewModel
                    .uiState
                    .value
                    .settingsBackStack
                    .size,
            )

            val synthesizedStack = listOf(AppRoutes.SettingsRoute, TestProfileRoute)
            router.emitCommand(NavigationCommand.OpenInTab(tab = 2, stack = synthesizedStack))

            assertEquals(ShellTab.Settings, viewModel.uiState.value.selectedTab)
            assertEquals(synthesizedStack, viewModel.uiState.value.settingsBackStack)
        }

    @Test
    fun `OpenInTab whose destination is already on top is a no-op with state unchanged`() =
        coroutineRule.runTest {
            val router = FakeDeepLinkRouter()
            val viewModel = createViewModel(router = router)

            // Default state: selectedTab = Settings (2), settingsBackStack = [SettingsRoute]
            val initialState = viewModel.uiState.value
            router.emitCommand(NavigationCommand.OpenInTab(tab = 2, stack = listOf(AppRoutes.SettingsRoute)))

            assertSame(initialState, viewModel.uiState.value)
        }

    @Test
    fun `OpenFullScreen calls navigator navigateTo and leaves tab stacks untouched`() =
        coroutineRule.runTest {
            val router = FakeDeepLinkRouter()
            val navigator = Navigator(AppRoutes.ShellRoute)
            val viewModel = createViewModel(router = router, navigator = navigator)

            val homeBefore = viewModel.uiState.value.homeBackStack
            val scannerBefore = viewModel.uiState.value.scannerBackStack
            val settingsBefore = viewModel.uiState.value.settingsBackStack

            router.emitCommand(NavigationCommand.OpenFullScreen(TestFullScreenRoute))

            assertEquals(listOf(AppRoutes.ShellRoute, TestFullScreenRoute), navigator.backStack.toList())
            assertEquals(homeBefore, viewModel.uiState.value.homeBackStack)
            assertEquals(scannerBefore, viewModel.uiState.value.scannerBackStack)
            assertEquals(settingsBefore, viewModel.uiState.value.settingsBackStack)
        }

    @Test
    fun `OpenFullScreen is a no-op when destination is already on top of root stack`() =
        coroutineRule.runTest {
            val router = FakeDeepLinkRouter()
            val navigator = Navigator(AppRoutes.ShellRoute)
            val viewModel = createViewModel(router = router, navigator = navigator)

            router.emitCommand(NavigationCommand.OpenFullScreen(TestFullScreenRoute))
            assertEquals(2, navigator.backStack.size)

            // Emitting same destination again
            router.emitCommand(NavigationCommand.OpenFullScreen(TestFullScreenRoute))
            assertEquals(2, navigator.backStack.size)
        }

    @Test
    fun `OpenInCurrentTab appends to selected tab only`() =
        coroutineRule.runTest {
            val router = FakeDeepLinkRouter()
            val viewModel = createViewModel(router = router)

            assertEquals(ShellTab.Settings, viewModel.uiState.value.selectedTab)
            val initialSettingsStack = viewModel.uiState.value.settingsBackStack
            val initialHomeStack = viewModel.uiState.value.homeBackStack

            router.emitCommand(NavigationCommand.OpenInCurrentTab(TestProfileRoute))

            assertEquals(initialSettingsStack + TestProfileRoute, viewModel.uiState.value.settingsBackStack)
            assertEquals(initialHomeStack, viewModel.uiState.value.homeBackStack)
        }

    @Test
    fun `OpenInCurrentTab is a no-op when destination is already on top of current tab`() =
        coroutineRule.runTest {
            val router = FakeDeepLinkRouter()
            val viewModel = createViewModel(router = router)

            val initialState = viewModel.uiState.value
            // SettingsRoute is already top of Settings tab
            router.emitCommand(NavigationCommand.OpenInCurrentTab(AppRoutes.SettingsRoute))

            assertSame(initialState, viewModel.uiState.value)
        }

    @Test
    fun `out-of-range tab index in OpenInTab is ignored without crashing`() =
        coroutineRule.runTest {
            val router = FakeDeepLinkRouter()
            val viewModel = createViewModel(router = router)

            val initialState = viewModel.uiState.value
            router.emitCommand(NavigationCommand.OpenInTab(tab = 99, stack = listOf(TestProfileRoute)))

            assertSame(initialState, viewModel.uiState.value)
        }

    @Test
    fun `two commands in sequence are both executed in order`() =
        coroutineRule.runTest {
            val router = FakeDeepLinkRouter()
            val viewModel = createViewModel(router = router)

            val homeStack = listOf(HomeStubRoute, TestProfileRoute)
            val settingsStack = listOf(AppRoutes.SettingsRoute, TestDetailsRoute)

            router.emitCommand(NavigationCommand.OpenInTab(tab = 0, stack = homeStack))
            router.emitCommand(NavigationCommand.OpenInTab(tab = 2, stack = settingsStack))

            assertEquals(ShellTab.Settings, viewModel.uiState.value.selectedTab)
            assertEquals(homeStack, viewModel.uiState.value.homeBackStack)
            assertEquals(settingsStack, viewModel.uiState.value.settingsBackStack)
        }

    @Test
    fun `EnsureModule adds module to installingModules, updates readyModules on success and replays deep link`() =
        coroutineRule.runTest {
            val installer = FakeFeatureInstaller()
            val router = FakeDeepLinkRouter()
            val viewModel = createViewModel(installer = installer, router = router)

            router.emitCommand(NavigationCommand.EnsureModule(module = "scanner", replay = "myapp://scanner/scan"))

            assertEquals(listOf("scanner"), installer.requestedModules)
            assertTrue("scanner" in viewModel.uiState.value.installingModules)
            assertFalse("scanner" in viewModel.uiState.value.readyModules)
            assertTrue(router.dispatchedUris.isEmpty())

            installer.release()

            assertFalse("scanner" in viewModel.uiState.value.installingModules)
            assertTrue("scanner" in viewModel.uiState.value.readyModules)
            assertEquals(listOf("myapp://scanner/scan"), router.dispatchedUris)
        }

    @Test
    fun `EnsureModule on failure clears installingModules, leaves readyModules empty, and emits ShowMessage`() =
        coroutineRule.runTest {
            val installer = FakeFeatureInstaller()
            val router = FakeDeepLinkRouter()
            val viewModel = createViewModel(installer = installer, router = router)

            installer.failure = IllegalStateException("Module download failed")

            viewModel.event.test {
                router.emitCommand(NavigationCommand.EnsureModule(module = "scanner", replay = "myapp://scanner/scan"))
                installer.release()

                assertFalse("scanner" in viewModel.uiState.value.installingModules)
                assertFalse("scanner" in viewModel.uiState.value.readyModules)
                assertTrue(router.dispatchedUris.isEmpty())

                val event = awaitItem() as ShellEvent.ShowMessage
                assertEquals(R.string.deeplink_error_install_failed, event.messageRes)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `Failed with Malformed emits no ShowMessage and does not mutate back stacks`() =
        coroutineRule.runTest {
            val router = FakeDeepLinkRouter()
            val viewModel = createViewModel(router = router)

            val initialHome = viewModel.uiState.value.homeBackStack
            val initialScanner = viewModel.uiState.value.scannerBackStack
            val initialSettings = viewModel.uiState.value.settingsBackStack

            viewModel.event.test {
                router.emitCommand(NavigationCommand.Failed("myapp://malformed", FailureReason.Malformed))
                expectNoEvents()
                cancelAndConsumeRemainingEvents()
            }

            assertEquals(initialHome, viewModel.uiState.value.homeBackStack)
            assertEquals(initialScanner, viewModel.uiState.value.scannerBackStack)
            assertEquals(initialSettings, viewModel.uiState.value.settingsBackStack)
        }

    @Test
    fun `Failed with NoResolver emits ShowMessage and does not mutate back stacks`() =
        coroutineRule.runTest {
            val router = FakeDeepLinkRouter()
            val viewModel = createViewModel(router = router)

            val initialHome = viewModel.uiState.value.homeBackStack
            val initialScanner = viewModel.uiState.value.scannerBackStack
            val initialSettings = viewModel.uiState.value.settingsBackStack

            viewModel.event.test {
                router.emitCommand(NavigationCommand.Failed("myapp://unknown", FailureReason.NoResolver))
                val event = awaitItem() as ShellEvent.ShowMessage
                assertEquals(R.string.deeplink_error_unknown_feature, event.messageRes)
                cancelAndConsumeRemainingEvents()
            }

            assertEquals(initialHome, viewModel.uiState.value.homeBackStack)
            assertEquals(initialScanner, viewModel.uiState.value.scannerBackStack)
            assertEquals(initialSettings, viewModel.uiState.value.settingsBackStack)
        }

    @Test
    fun `Failed with Blocked emits ShowMessage and does not mutate back stacks`() =
        coroutineRule.runTest {
            val router = FakeDeepLinkRouter()
            val viewModel = createViewModel(router = router)

            val initialHome = viewModel.uiState.value.homeBackStack
            val initialScanner = viewModel.uiState.value.scannerBackStack
            val initialSettings = viewModel.uiState.value.settingsBackStack

            viewModel.event.test {
                router.emitCommand(NavigationCommand.Failed("myapp://settings/secret", FailureReason.Blocked))
                val event = awaitItem() as ShellEvent.ShowMessage
                assertEquals(R.string.deeplink_error_blocked, event.messageRes)
                cancelAndConsumeRemainingEvents()
            }

            assertEquals(initialHome, viewModel.uiState.value.homeBackStack)
            assertEquals(initialScanner, viewModel.uiState.value.scannerBackStack)
            assertEquals(initialSettings, viewModel.uiState.value.settingsBackStack)
        }

    @Test
    fun `Failed with RedirectLoop emits no ShowMessage and does not mutate back stacks`() =
        coroutineRule.runTest {
            val router = FakeDeepLinkRouter()
            val viewModel = createViewModel(router = router)

            val initialHome = viewModel.uiState.value.homeBackStack
            val initialScanner = viewModel.uiState.value.scannerBackStack
            val initialSettings = viewModel.uiState.value.settingsBackStack

            viewModel.event.test {
                router.emitCommand(NavigationCommand.Failed("myapp://loop", FailureReason.RedirectLoop))
                expectNoEvents()
                cancelAndConsumeRemainingEvents()
            }

            assertEquals(initialHome, viewModel.uiState.value.homeBackStack)
            assertEquals(initialScanner, viewModel.uiState.value.scannerBackStack)
            assertEquals(initialSettings, viewModel.uiState.value.settingsBackStack)
        }
}
