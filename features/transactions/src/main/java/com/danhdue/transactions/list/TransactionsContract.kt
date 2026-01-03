/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.transactions.list

data class TransactionsViewState(
    val resultCount: Int? = null,
)

sealed class TransactionsViewEvent {
    data class LoadTransactions(val page: Int) : TransactionsViewEvent()
}