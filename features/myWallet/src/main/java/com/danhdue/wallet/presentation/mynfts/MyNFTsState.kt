/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.wallet.presentation.mynfts

import com.danhdue.wallet.presentation.model.MyNFTsUiModel

/**
 * Represents the state of the MyNFTs screen.
 *
 * @property isLoading True if data is currently being loaded.
 * @property items The list of UI models to be displayed.
 */
data class MyNFTsState(
    val isLoading: Boolean = false,
    val items: List<MyNFTsUiModel> = emptyList(),
)
