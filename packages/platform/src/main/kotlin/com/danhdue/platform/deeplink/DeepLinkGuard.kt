/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform.deeplink

/**
 * Result returned by a [DeepLinkGuard] after inspecting an incoming deep link and target.
 */
sealed interface GuardVerdict {
    /** The navigation is permitted to proceed. */
    data object Allow : GuardVerdict

    /**
     * The navigation must be redirected to [to] (a raw URI string, such as a login link).
     *
     * The router will re-dispatch [to], capped at depth 3 to prevent redirect loops.
     */
    data class Redirect(
        val to: String,
    ) : GuardVerdict

    /**
     * The navigation is blocked. The router will emit [NavigationCommand.Failed] with [FailureReason.Blocked].
     *
     * @property reason Human-readable explanation of why the link was blocked.
     */
    data class Block(
        val reason: String,
    ) : GuardVerdict
}

/**
 * Interceptor in the deep link dispatch pipeline.
 *
 * **Contract Obligation — Guards MUST be idempotent.**
 * The router calls the guard chain twice for links targeting an auth-gated dynamic feature split:
 * once as a pre-gate before initiating split download (to avoid making an unauthenticated user download
 * code they cannot use), and once fully after the split is loaded and the destination is resolved.
 * Therefore, calling [check] multiple times for the same link must produce consistent verdicts
 * without side-effect duplication.
 */
interface DeepLinkGuard {
    /**
     * Evaluation order. Guards with lower order values are evaluated first.
     *
     * Default is 0.
     */
    val order: Int get() = 0

    /**
     * Evaluates [link] and [target] to determine whether navigation can proceed.
     */
    suspend fun check(
        link: DeepLink,
        target: DeepLinkTarget,
    ): GuardVerdict
}
