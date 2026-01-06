/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.transactions.presentation.transactiondetail

import com.danhdue.transactions.presentation.transactiondetail.model.TransactionDetailUiModel

/**
 * Represents the state of the TransactionDetail screen.
 *
 * @property isLoading True if data is currently being loaded.
 * @property items The list of UI models to be displayed.
 */
data class TransactionDetailState(
    val isLoading: Boolean = false,
    val items: List<TransactionDetailUiModel> = emptyList(),
)
