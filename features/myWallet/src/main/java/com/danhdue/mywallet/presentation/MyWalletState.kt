/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.mywallet.presentation

import com.danhdue.mywallet.presentation.model.MyWalletUiModel

/**
 * Represents the state of the MyWallet screen.
 *
 * @property isLoading True if data is currently being loaded.
 * @property items The list of UI models to be displayed.
 */
data class MyWalletState(
    val isLoading: Boolean = false,
    val items: List<MyWalletUiModel> = emptyList(),
)
