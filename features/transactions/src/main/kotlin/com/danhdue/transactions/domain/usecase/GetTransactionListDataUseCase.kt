/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.transactions.domain.usecase

import com.danhdue.transactions.domain.model.TransactionList
import javax.inject.Inject

/**
 * Use case that encapsulates the business logic for fetching the TransactionList feature data.
 */
class GetTransactionListDataUseCase
    @Inject
    constructor(
        private val repository: TransactionListRepository,
    ) {
        /**
         * Executes the use case.
         */
        suspend operator fun invoke(): Result<TransactionList> = repository.getTransactionListData()
    }
