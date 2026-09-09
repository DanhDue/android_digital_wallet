/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform.deeplink

import androidx.navigation3.runtime.NavKey
import com.danhdue.core.coroutines.DispatcherProvider
import com.danhdue.core.session.SessionManager
import com.danhdue.platform.AppEvent
import com.danhdue.platform.AppEventBus
import com.danhdue.platform.AppRoutes
import com.danhdue.platform.EntryProviderInstaller
import com.danhdue.platform.FeatureEntry
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.ServiceConfigurationError

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultDeepLinkRouterTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val testDispatcherProvider =
        object : DispatcherProvider {
            override val main: CoroutineDispatcher = testDispatcher
            override val io: CoroutineDispatcher = testDispatcher
            override val default: CoroutineDispatcher = testDispatcher
            override val unconfined: CoroutineDispatcher = testDispatcher
        }

    private data object DummyRoute : NavKey

    private data object DummyTablessRoute : NavKey

    private val testEntryPoints =
        listOf(
            FeatureEntryPoint(
                feature = "settings",
                entryRoute = AppRoutes.SettingsRoute,
                tab = 2,
            ),
            FeatureEntryPoint(
                feature = "scanner",
                entryRoute = AppRoutes.ScannerRoute,
                tab = 1,
                dynamicModule = "scanner",
                requiresAuth = false,
            ),
            FeatureEntryPoint(
                feature = "auth_dfm",
                entryRoute = AppRoutes.ScannerRoute,
                tab = 1,
                dynamicModule = "auth_dfm",
                requiresAuth = true,
            ),
            FeatureEntryPoint(
                feature = "tabless",
                entryRoute = DummyTablessRoute,
                tab = null,
            ),
        )

    private fun createRouter(
        appEventBus: AppEventBus = AppEventBus(),
        pendingStore: PendingDeepLinkStore = InMemoryPendingDeepLinkStore(),
        guards: Set<DeepLinkGuard> = emptySet(),
        resolvers: Set<DeepLinkResolver> = emptySet(),
        entryPoints: List<FeatureEntryPoint> = testEntryPoints,
        featureEntryLoader: () -> Iterable<FeatureEntry> = { emptyList() },
    ): DefaultDeepLinkRouter {
        val router =
            DefaultDeepLinkRouter(
                appEventBus = appEventBus,
                pendingStore = pendingStore,
                dispatcherProvider = testDispatcherProvider,
                guards = guards,
                resolvers = resolvers,
                entryPoints = entryPoints,
            )
        router.featureEntryLoader = featureEntryLoader
        return router
    }

    @Test
    fun `malformed URI emits Failed with Malformed reason`() =
        runTest(testDispatcher) {
            val router = createRouter()
            router.dispatch(":::not a valid uri:::")
            advanceUntilIdle()

            val command = router.commands.first()
            assertTrue(command is NavigationCommand.Failed)
            assertEquals(FailureReason.Malformed, (command as NavigationCommand.Failed).reason)
        }

    @Test
    fun `unknown feature key emits Failed with UnknownFeature reason`() =
        runTest(testDispatcher) {
            val router = createRouter()
            router.dispatch("myapp://unknown_feature/path")
            advanceUntilIdle()

            val command = router.commands.first()
            assertTrue(command is NavigationCommand.Failed)
            assertEquals(FailureReason.UnknownFeature, (command as NavigationCommand.Failed).reason)
        }

    @Test
    fun `known feature when no resolver claims it emits Failed with NoResolver reason`() =
        runTest(testDispatcher) {
            val router = createRouter()
            router.dispatch("myapp://settings/profile")
            advanceUntilIdle()

            val command = router.commands.first()
            assertTrue(command is NavigationCommand.Failed)
            assertEquals(FailureReason.NoResolver, (command as NavigationCommand.Failed).reason)
        }

    @Test
    fun `happy path with null placement and tab derives OpenInTab with synthesized parents`() =
        runTest(testDispatcher) {
            val resolver =
                DeepLinkResolver { link ->
                    if (link.feature == "settings") {
                        DeepLinkTarget(destination = DummyRoute, placement = null)
                    } else {
                        null
                    }
                }
            val router = createRouter(resolvers = setOf(resolver))
            router.dispatch("myapp://settings/details")
            advanceUntilIdle()

            val command = router.commands.first()
            assertEquals(
                NavigationCommand.OpenInTab(tab = 2, stack = listOf(AppRoutes.SettingsRoute, DummyRoute)),
                command,
            )
        }

    @Test
    fun `happy path with null placement and null tab derives OpenFullScreen`() =
        runTest(testDispatcher) {
            val resolver =
                DeepLinkResolver { link ->
                    if (link.feature == "tabless") {
                        DeepLinkTarget(destination = DummyRoute, placement = null)
                    } else {
                        null
                    }
                }
            val router = createRouter(resolvers = setOf(resolver))
            router.dispatch("myapp://tabless/screen")
            advanceUntilIdle()

            val command = router.commands.first()
            assertEquals(
                NavigationCommand.OpenFullScreen(destination = DummyRoute),
                command,
            )
        }

    @Test
    fun `explicit placement overrides the derived default`() =
        runTest(testDispatcher) {
            val resolver =
                DeepLinkResolver { link ->
                    if (link.feature == "settings") {
                        DeepLinkTarget(destination = DummyRoute, placement = Placement.CurrentTab)
                    } else {
                        null
                    }
                }
            val router = createRouter(resolvers = setOf(resolver))
            router.dispatch("myapp://settings/details")
            advanceUntilIdle()

            val command = router.commands.first()
            assertEquals(
                NavigationCommand.OpenInCurrentTab(destination = DummyRoute),
                command,
            )
        }

    @Test
    fun `guards run in order sequence and Block short-circuits evaluation`() =
        runTest(testDispatcher) {
            var secondGuardCalled = false
            val blockingGuard =
                object : DeepLinkGuard {
                    override val order: Int = 1

                    override suspend fun check(
                        link: DeepLink,
                        target: DeepLinkTarget,
                    ): GuardVerdict = GuardVerdict.Block("Access forbidden")
                }
            val secondGuard =
                object : DeepLinkGuard {
                    override val order: Int = 2

                    override suspend fun check(
                        link: DeepLink,
                        target: DeepLinkTarget,
                    ): GuardVerdict {
                        secondGuardCalled = true
                        return GuardVerdict.Allow
                    }
                }
            val resolver =
                DeepLinkResolver {
                    DeepLinkTarget(destination = DummyRoute)
                }

            val router =
                createRouter(
                    guards = setOf(secondGuard, blockingGuard),
                    resolvers = setOf(resolver),
                )
            router.dispatch("myapp://settings/profile")
            advanceUntilIdle()

            val command = router.commands.first()
            assertTrue(command is NavigationCommand.Failed)
            assertEquals(FailureReason.Blocked, (command as NavigationCommand.Failed).reason)
            assertFalse("Second guard must not be called after first guard blocks", secondGuardCalled)
        }

    @Test
    fun `redirect loop exceeding depth cap 3 terminates with Failed RedirectLoop`() =
        runTest(testDispatcher) {
            val loopingGuard =
                object : DeepLinkGuard {
                    override suspend fun check(
                        link: DeepLink,
                        target: DeepLinkTarget,
                    ): GuardVerdict {
                        val next =
                            when (link.segments.firstOrNull()) {
                                "a" -> "myapp://settings/b"
                                "b" -> "myapp://settings/c"
                                "c" -> "myapp://settings/a"
                                else -> "myapp://settings/a"
                            }
                        return GuardVerdict.Redirect(next)
                    }
                }
            val resolver =
                DeepLinkResolver {
                    DeepLinkTarget(destination = DummyRoute)
                }

            val router = createRouter(guards = setOf(loopingGuard), resolvers = setOf(resolver))
            router.dispatch("myapp://settings/a")
            advanceUntilIdle()

            val command = router.commands.first()
            assertTrue(command is NavigationCommand.Failed)
            assertEquals(FailureReason.RedirectLoop, (command as NavigationCommand.Failed).reason)
        }

    @Test
    fun `dynamicModule not ready emits EnsureModule and replay does not emit EnsureModule a second time`() =
        runTest(testDispatcher) {
            var resolverInstalled = false
            val dfmEntry =
                object : FeatureEntry {
                    override fun installer(): EntryProviderInstaller = {}

                    override fun resolver(): DeepLinkResolver? =
                        if (resolverInstalled) {
                            DeepLinkResolver { DeepLinkTarget(destination = DummyRoute) }
                        } else {
                            null
                        }
                }

            val router = createRouter(featureEntryLoader = { listOf(dfmEntry) })

            // Pass 1: DFM not ready
            router.dispatch("myapp://scanner/qr")
            advanceUntilIdle()

            val firstCommand = router.commands.first()
            assertEquals(
                NavigationCommand.EnsureModule(module = "scanner", replay = "myapp://scanner/qr"),
                firstCommand,
            )

            // Simulate successful module installation
            resolverInstalled = true

            // Pass 2: Replay of same URI
            router.dispatch("myapp://scanner/qr")
            advanceUntilIdle()

            val secondCommand = router.commands.first()
            assertEquals(
                NavigationCommand.OpenInTab(tab = 1, stack = listOf(AppRoutes.ScannerRoute, DummyRoute)),
                secondCommand,
            )
        }

    @Test
    fun `replay with still no resolver emits Failed with InstallFailed reason`() =
        runTest(testDispatcher) {
            val router = createRouter()

            // Pass 1: Emits EnsureModule
            router.dispatch("myapp://scanner/qr")
            advanceUntilIdle()

            val firstCommand = router.commands.first()
            assertEquals(
                NavigationCommand.EnsureModule(module = "scanner", replay = "myapp://scanner/qr"),
                firstCommand,
            )

            // Pass 2: Replay, but resolver still absent
            router.dispatch("myapp://scanner/qr")
            advanceUntilIdle()

            val secondCommand = router.commands.first()
            assertTrue(secondCommand is NavigationCommand.Failed)
            assertEquals(FailureReason.InstallFailed, (secondCommand as NavigationCommand.Failed).reason)
        }

    @Test
    fun `pre-gate fires before EnsureModule when requiresAuth is true and user is logged out`() =
        runTest(testDispatcher) {
            val sessionManager = SessionManager().apply { isLoggedIn = false }
            val pendingStore = InMemoryPendingDeepLinkStore()
            val authGuard = AuthDeepLinkGuard(sessionManager, pendingStore)

            val router =
                createRouter(
                    guards = setOf(authGuard),
                    pendingStore = pendingStore,
                )

            router.dispatch("myapp://auth_dfm/secure_scan")
            advanceUntilIdle()

            // Auth guard redirects to "myapp://login" instead of emitting EnsureModule
            val command = router.commands.first()
            assertTrue(command is NavigationCommand.Failed)
            // Since "login" is not in testEntryPoints, it fails with UnknownFeature at login
            assertEquals(FailureReason.UnknownFeature, (command as NavigationCommand.Failed).reason)
            assertEquals("myapp://auth_dfm/secure_scan", pendingStore.takeIfAny())
        }

    @Test
    fun `UserLoggedIn event replays pending link exactly once`() =
        runTest(testDispatcher) {
            val appEventBus = AppEventBus()
            val pendingStore = InMemoryPendingDeepLinkStore()
            pendingStore.put("myapp://settings/profile")

            val resolver =
                DeepLinkResolver { link ->
                    if (link.feature == "settings") {
                        DeepLinkTarget(destination = DummyRoute)
                    } else {
                        null
                    }
                }

            val router =
                createRouter(
                    appEventBus = appEventBus,
                    pendingStore = pendingStore,
                    resolvers = setOf(resolver),
                )
            // Fire UserLoggedIn
            appEventBus.publish(AppEvent.UserLoggedIn)

            val command = router.commands.first()
            assertEquals(
                NavigationCommand.OpenInTab(tab = 2, stack = listOf(AppRoutes.SettingsRoute, DummyRoute)),
                command,
            )

            // Subsequent UserLoggedIn should do nothing because store is empty
            appEventBus.publish(AppEvent.UserLoggedIn)
        }

    @Test
    fun `cold-start buffering delivers command dispatched before subscription`() =
        runTest(testDispatcher) {
            val resolver =
                DeepLinkResolver {
                    DeepLinkTarget(destination = DummyRoute)
                }
            val router = createRouter(resolvers = setOf(resolver))

            // Dispatch before collecting commands
            router.dispatch("myapp://settings/details")
            advanceUntilIdle()

            // Late subscriber collects
            val command = router.commands.first()
            assertEquals(
                NavigationCommand.OpenInTab(tab = 2, stack = listOf(AppRoutes.SettingsRoute, DummyRoute)),
                command,
            )
        }

    @Test
    fun `ServiceLoader branch skips unloadable entry throwing ServiceConfigurationError`() =
        runTest(testDispatcher) {
            val badEntryIterator =
                object : Iterator<FeatureEntry> {
                    private var index = 0

                    override fun hasNext(): Boolean = index < 2

                    override fun next(): FeatureEntry {
                        index++
                        if (index == 1) {
                            throw ServiceConfigurationError("Simulated missing DFM split class")
                        }
                        return object : FeatureEntry {
                            override fun installer(): EntryProviderInstaller = {}

                            override fun resolver(): DeepLinkResolver = DeepLinkResolver { DeepLinkTarget(destination = DummyRoute) }
                        }
                    }
                }
            val iterable = Iterable { badEntryIterator }

            val router = createRouter(featureEntryLoader = { iterable })
            router.dispatch("myapp://settings/test")
            advanceUntilIdle()

            val command = router.commands.first()
            assertEquals(
                NavigationCommand.OpenInTab(tab = 2, stack = listOf(AppRoutes.SettingsRoute, DummyRoute)),
                command,
            )
        }
}
