/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.wallet.presentation

import com.danhdue.wallet.presentation.model.MyWalletUiModel

/**
 * Represents the state of the MyWallet screen.
 *
 * @property isLoading True if data is currently being loaded.
 * @property items The list of UI models to be displayed.
 */
data class MyWalletState(
    val isLoading: Boolean = false,
    val accountName: String = "Account 1",
    val totalBalance: String = "$0.00",
    val isBalanceVisible: Boolean = true,
    val walletAddress: String = "",
    val profileImageUrl: String = "",
    val networkName: String = "Smart Chain",
    val selectedTabIndex: Int = 0,
    val items: List<MyWalletUiModel> = emptyList(),
)
