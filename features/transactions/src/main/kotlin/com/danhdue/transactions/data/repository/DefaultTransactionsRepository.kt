/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.transactions.data.repository

import com.danhdue.transactions.domain.model.TransactionDetail
import com.danhdue.transactions.domain.model.TransactionList
import com.danhdue.transactions.domain.model.Transactions
import com.danhdue.transactions.domain.repository.TransactionsRepository
import javax.inject.Inject

/**
 * Concrete implementation of the repository for the Transactions feature.
 */
@Suppress("StringLiteralDuplication")
class DefaultTransactionsRepository
    @Inject
    constructor() : TransactionsRepository {
        override suspend fun getTransactionsData(): Result<Transactions> =
            try {
                val domainModel = Transactions(id = "1", data = "Sample data from repository")
                Result.success(domainModel)
            } catch (e: Exception) {
                Result.failure(e)
            }

        override suspend fun getTransactionDetailData(): Result<TransactionDetail> =
            try {
                val domainModel = TransactionDetail(id = "1", data = "Sample data from repository")
                Result.success(domainModel)
            } catch (e: Exception) {
                Result.failure(e)
            }

        override suspend fun getTransactionListData(): Result<TransactionList> =
            try {
                val domainModel = TransactionList(id = "1", data = "Sample data from repository")
                Result.success(domainModel)
            } catch (e: Exception) {
                Result.failure(e)
            }
    }
