/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.transactions.presentation.transactionlist.model

/**
 * Represents the UI model for a single item in the TransactionList feature.
 * This class is optimized for display in the Presentation Layer.
 */
data class TransactionListUiModel(
    val id: String,
    val title: String,
    val description: String,
)
