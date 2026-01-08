/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.home.presentation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.danhdue.mywallet.presentation.MyWalletRoute
import com.danhdue.scanner.presentation.ScannerRoute
import com.danhdue.settings.presentation.SettingsRoute
import com.danhdue.transactions.presentation.transactionlist.TransactionListRoute
import com.danhdue.trends.presentation.TrendsRoute

/**
 * Represents the state of the Home (Main Tabbed) screen.
 */
data class HomeState(
    val selectedTab: HomeTab = HomeTab.Wallet,
    // Store backstacks for each tab to support nested navigation
    val walletBackStack: SnapshotStateList<Any> = mutableStateListOf(MyWalletRoute),
    val transactionsBackStack: SnapshotStateList<Any> = mutableStateListOf(TransactionListRoute),
    val scannerBackStack: SnapshotStateList<Any> = mutableStateListOf(ScannerRoute),
    val trendsBackStack: SnapshotStateList<Any> = mutableStateListOf(TrendsRoute),
    val settingsBackStack: SnapshotStateList<Any> = mutableStateListOf(SettingsRoute)
)

sealed class HomeTab(val index: Int) {
    data object Wallet : HomeTab(0)
    data object Transactions : HomeTab(1)
    data object Scanner : HomeTab(2)
    data object Trends : HomeTab(3)
    data object Settings : HomeTab(4)
}
