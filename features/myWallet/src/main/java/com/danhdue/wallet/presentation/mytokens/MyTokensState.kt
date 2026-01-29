/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.wallet.presentation.mytokens

import com.danhdue.wallet.presentation.model.MyTokensUiModel

/**
 * Represents the state of the MyTokens screen.
 *
 * @property isLoading True if data is currently being loaded.
 * @property items The list of UI models to be displayed.
 */
data class MyTokensState(
    val isLoading: Boolean = false,
    val items: List<MyTokensUiModel> = emptyList(),
)
