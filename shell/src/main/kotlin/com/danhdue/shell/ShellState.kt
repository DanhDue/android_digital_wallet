/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.shell

import com.danhdue.platform.AppRoutes

/**
 * Represents the state of the Shell (the Host's main tabbed container).
 * All properties are immutable to follow MVI pattern.
 *
 * Each tab's initial nested backstack is seeded from a cross-feature `NavKey` in
 * `:platform.AppRoutes` (Task 11), so `:shell` no longer depends on any feature
 * module to build its tabs.
 *
 * @property profileName profile display name last reported by
 *   [com.danhdue.platform.AppEvent.ProfileNameChanged] — the Task 11 pilot's
 *   shell-owned label (rendered on the Settings bottom-bar tab), fed over
 *   [AppEventBus] without importing `com.danhdue.settings.*`. Blank until the
 *   Settings feature has loaded.
 */
data class ShellState(
    val selectedTab: ShellTab = ShellTab.Wallet,
    // Immutable backstacks for each tab to support nested navigation
    val walletBackStack: List<Any> = listOf(AppRoutes.MyWalletRoute),
    val transactionsBackStack: List<Any> = listOf(AppRoutes.TransactionListRoute),
    val scannerBackStack: List<Any> = listOf(AppRoutes.ScannerRoute),
    val trendsBackStack: List<Any> = listOf(AppRoutes.TrendsRoute),
    val settingsBackStack: List<Any> = listOf(AppRoutes.SettingsRoute),
    val profileName: String = "",
)

@Suppress("MagicNumber")
sealed class ShellTab(
    val index: Int,
) {
    data object Wallet : ShellTab(0)

    data object Transactions : ShellTab(1)

    data object Scanner : ShellTab(2)

    data object Trends : ShellTab(3)

    data object Settings : ShellTab(4)
}
