/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.transactions.presentation.transactiondetail

/**
 * Defines the one-off events that the ViewModel can send to the UI.
 * These events are meant to be consumed only once (e.g., navigation, snackbar).
 */
sealed interface TransactionDetailEvent {
    // Example: data class NavigateToDetails(val screenId: String) : TransactionDetailEvent
}
