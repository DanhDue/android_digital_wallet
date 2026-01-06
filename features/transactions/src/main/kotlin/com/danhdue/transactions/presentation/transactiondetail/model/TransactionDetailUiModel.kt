/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.transactions.presentation.transactiondetail.model

/**
 * Represents the UI model for a single item in the TransactionDetail feature.
 * This class is optimized for display in the Presentation Layer.
 */
data class TransactionDetailUiModel(
    val id: String,
    val title: String,
    val description: String,
)
