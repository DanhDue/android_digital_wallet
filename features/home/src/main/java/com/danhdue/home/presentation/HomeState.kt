/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.home.presentation

import com.danhdue.scanner.presentation.ScannerRoute
import com.danhdue.settings.presentation.SettingsRoute
import com.danhdue.transactions.presentation.transactionlist.TransactionListRoute
import com.danhdue.trends.presentation.TrendsRoute
import com.danhdue.wallet.presentation.MyWalletRoute

/**
 * Represents the state of the Home (Main Tabbed) screen.
 * All properties are immutable to follow MVI pattern.
 */
data class HomeState(
    val selectedTab: HomeTab = HomeTab.Wallet,
    // Immutable backstacks for each tab to support nested navigation
    val walletBackStack: List<Any> = listOf(MyWalletRoute),
    val transactionsBackStack: List<Any> = listOf(TransactionListRoute),
    val scannerBackStack: List<Any> = listOf(ScannerRoute),
    val trendsBackStack: List<Any> = listOf(TrendsRoute),
    val settingsBackStack: List<Any> = listOf(SettingsRoute),
)

@Suppress("MagicNumber")
sealed class HomeTab(
    val index: Int,
) {
    data object Wallet : HomeTab(0)

    data object Transactions : HomeTab(1)

    data object Scanner : HomeTab(2)

    data object Trends : HomeTab(3)

    data object Settings : HomeTab(4)
}
