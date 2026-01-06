/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.transactions.presentation.transactionlist

import com.danhdue.transactions.presentation.transactionlist.model.TransactionListUiModel

/**
 * Represents the state of the TransactionList screen.
 *
 * @property isLoading True if data is currently being loaded.
 * @property items The list of UI models to be displayed.
 */
data class TransactionListState(
    val isLoading: Boolean = false,
    val items: List<TransactionListUiModel> = emptyList(),
)
