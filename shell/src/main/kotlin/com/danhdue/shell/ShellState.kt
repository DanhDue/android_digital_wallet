/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.shell

import com.danhdue.scanner.presentation.ScannerRoute
import com.danhdue.settings.presentation.SettingsRoute
import com.danhdue.transactions.presentation.transactionlist.TransactionListRoute
import com.danhdue.trends.presentation.TrendsRoute
import com.danhdue.wallet.presentation.MyWalletRoute

/**
 * Represents the state of the Shell (the Host's main tabbed container).
 * All properties are immutable to follow MVI pattern.
 *
 * The five feature `*Route` imports below seed each tab's initial nested backstack.
 * They are the last remaining cross-feature coupling in the shell; Task 11 relocates
 * these `NavKey`s into `:platform.AppRoutes` and drops the imports.
 */
data class ShellState(
    val selectedTab: ShellTab = ShellTab.Wallet,
    // Immutable backstacks for each tab to support nested navigation
    val walletBackStack: List<Any> = listOf(MyWalletRoute),
    val transactionsBackStack: List<Any> = listOf(TransactionListRoute),
    val scannerBackStack: List<Any> = listOf(ScannerRoute),
    val trendsBackStack: List<Any> = listOf(TrendsRoute),
    val settingsBackStack: List<Any> = listOf(SettingsRoute),
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
