/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.transactions.domain.repository

import com.danhdue.transactions.domain.model.TransactionDetail
import com.danhdue.transactions.domain.model.TransactionList
import com.danhdue.transactions.domain.model.Transactions

/**
 * Interface defining the contract for the Transactions feature's repository.
 */
interface TransactionsRepository {
    /**
     * Retrieves data for the Transactions feature.
     *
     * @return A Result object containing the Transactions domain model on success,
     * or an exception on failure.
     */
    suspend fun getTransactionsData(): Result<Transactions>

    /**
     * Retrieves data for the TransactionList feature.
     *
     * @return A Result object containing the TransactionList domain model on success,
     * or an exception on failure.
     */
    suspend fun getTransactionListData(): Result<TransactionList>

    /**
     * Retrieves data for the TransactionDetail feature.
     *
     * @return A Result object containing the TransactionDetail domain model on success,
     * or an exception on failure.
     */
    suspend fun getTransactionDetailData(): Result<TransactionDetail>
}
