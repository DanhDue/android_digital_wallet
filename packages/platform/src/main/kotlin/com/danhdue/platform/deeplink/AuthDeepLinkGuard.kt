/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform.deeplink

import com.danhdue.core.session.SessionManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Authentication guard for deep link navigation.
 *
 * Checks if the resolved [DeepLinkTarget.requiresAuth] is true:
 * - If user is signed in: returns [GuardVerdict.Allow].
 * - If user is signed out: stores the link in [PendingDeepLinkStore] and returns [GuardVerdict.Redirect] with [LOGIN_URI].
 *
 * **Idempotency**: Calling [check] multiple times with the same unauthenticated link produces
 * identical [GuardVerdict.Redirect] results and leaves exactly one pending link in the store.
 */
@Singleton
class AuthDeepLinkGuard @Inject constructor(
    private val sessionManager: SessionManager,
    private val pendingDeepLinkStore: PendingDeepLinkStore,
) : DeepLinkGuard {
    override val order: Int = 0

    override suspend fun check(
        link: DeepLink,
        target: DeepLinkTarget,
    ): GuardVerdict {
        if (!target.requiresAuth) {
            return GuardVerdict.Allow
        }

        return if (sessionManager.isLoggedIn) {
            GuardVerdict.Allow
        } else {
            pendingDeepLinkStore.put(link.raw)
            GuardVerdict.Redirect(LOGIN_URI)
        }
    }

    companion object {
        const val LOGIN_URI = "myapp://login"
    }
}
