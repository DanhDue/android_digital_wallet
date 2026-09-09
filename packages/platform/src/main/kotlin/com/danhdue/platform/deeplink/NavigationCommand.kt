/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform.deeplink

import androidx.navigation3.runtime.NavKey

/**
 * Reasons why a deep link dispatch could fail without navigating.
 */
enum class FailureReason {
    Malformed,
    UnknownFeature,
    NoResolver,
    Blocked,
    RedirectLoop,
    InstallFailed,
}

/**
 * Navigation commands emitted by `DeepLinkRouter` and executed exclusively by the host shell.
 */
sealed interface NavigationCommand {
    /**
     * Switch to the specified [tab] and replace its nested back stack with [stack].
     */
    data class OpenInTab(
        val tab: Int,
        val stack: List<NavKey>,
    ) : NavigationCommand

    /**
     * Navigate to [destination] on the root back stack (above tabs).
     */
    data class OpenFullScreen(
        val destination: NavKey,
    ) : NavigationCommand

    /**
     * Append [destination] to the current active tab's nested back stack without switching tabs.
     */
    data class OpenInCurrentTab(
        val destination: NavKey,
    ) : NavigationCommand

    /**
     * Request the host shell to ensure an on-demand dynamic split [module] is installed,
     * then replay [replay] deep link upon completion.
     */
    data class EnsureModule(
        val module: String,
        val replay: String,
    ) : NavigationCommand

    /**
     * Report that deep link dispatch failed for [reason], without modifying any navigation state.
     */
    data class Failed(
        val raw: String,
        val reason: FailureReason,
    ) : NavigationCommand
}
