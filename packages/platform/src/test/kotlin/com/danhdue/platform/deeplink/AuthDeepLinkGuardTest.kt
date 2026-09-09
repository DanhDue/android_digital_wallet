/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform.deeplink

import com.danhdue.core.session.SessionManager
import com.danhdue.platform.AppRoutes
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [AuthDeepLinkGuard], including contract idempotency verification.
 */
class AuthDeepLinkGuardTest {
    private lateinit var sessionManager: SessionManager
    private lateinit var pendingStore: InMemoryPendingDeepLinkStore
    private lateinit var guard: AuthDeepLinkGuard

    @Before
    fun setUp() {
        sessionManager = SessionManager()
        pendingStore = InMemoryPendingDeepLinkStore()
        guard = AuthDeepLinkGuard(sessionManager, pendingStore)
    }

    @Test
    fun `requiresAuth false yields Allow regardless of sign-in state`() =
        runTest {
            val link =
                DeepLink(
                    raw = "myapp://settings",
                    feature = "settings",
                    segments = emptyList(),
                    params = emptyMap(),
                )
            val target =
                DeepLinkTarget(
                    destination = AppRoutes.SettingsRoute,
                    requiresAuth = false,
                )

            sessionManager.isLoggedIn = false
            assertEquals(GuardVerdict.Allow, guard.check(link, target))
            assertNull(pendingStore.takeIfAny())

            sessionManager.isLoggedIn = true
            assertEquals(GuardVerdict.Allow, guard.check(link, target))
            assertNull(pendingStore.takeIfAny())
        }

    @Test
    fun `requiresAuth true and signed in yields Allow and stores nothing`() =
        runTest {
            val link =
                DeepLink(
                    raw = "myapp://scanner",
                    feature = "scanner",
                    segments = emptyList(),
                    params = emptyMap(),
                )
            val target =
                DeepLinkTarget(
                    destination = AppRoutes.ScannerRoute,
                    requiresAuth = true,
                )

            sessionManager.isLoggedIn = true
            val verdict = guard.check(link, target)

            assertEquals(GuardVerdict.Allow, verdict)
            assertNull(pendingStore.takeIfAny())
        }

    @Test
    fun `requiresAuth true and signed out yields Redirect to login and stores link`() =
        runTest {
            val link =
                DeepLink(
                    raw = "myapp://scanner/qr?id=42",
                    feature = "scanner",
                    segments = listOf("qr"),
                    params = mapOf("id" to "42"),
                )
            val target =
                DeepLinkTarget(
                    destination = AppRoutes.ScannerRoute,
                    requiresAuth = true,
                )

            sessionManager.isLoggedIn = false
            val verdict = guard.check(link, target)

            assertTrue("Verdict must be Redirect", verdict is GuardVerdict.Redirect)
            assertEquals(AuthDeepLinkGuard.LOGIN_URI, (verdict as GuardVerdict.Redirect).to)
            assertEquals("myapp://scanner/qr?id=42", pendingStore.takeIfAny())
        }

    @Test
    fun `guard is idempotent when called multiple times with identical arguments`() =
        runTest {
            val link =
                DeepLink(
                    raw = "myapp://scanner/qr",
                    feature = "scanner",
                    segments = listOf("qr"),
                    params = emptyMap(),
                )
            val target =
                DeepLinkTarget(
                    destination = AppRoutes.ScannerRoute,
                    requiresAuth = true,
                )

            sessionManager.isLoggedIn = false

            // Pre-gate execution (first call)
            val firstVerdict = guard.check(link, target)
            assertEquals(GuardVerdict.Redirect(AuthDeepLinkGuard.LOGIN_URI), firstVerdict)

            // Full gate execution (second call with identical arguments)
            val secondVerdict = guard.check(link, target)
            assertEquals(GuardVerdict.Redirect(AuthDeepLinkGuard.LOGIN_URI), secondVerdict)

            // Verifying exactly one link is stored (take-once returns the link, next call returns null)
            val retrieved = pendingStore.takeIfAny()
            assertNotNull(retrieved)
            assertEquals("myapp://scanner/qr", retrieved)
            assertNull("Second take must return null; exactly one link was stored", pendingStore.takeIfAny())
        }
}
