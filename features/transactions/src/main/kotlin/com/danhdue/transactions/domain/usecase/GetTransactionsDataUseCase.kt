/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.transactions.domain.usecase

import com.danhdue.transactions.domain.model.Transactions
import com.danhdue.transactions.domain.repository.TransactionsRepository
import javax.inject.Inject

/**
 * Use case that encapsulates the business logic for fetching the Transactions feature data.
 */
class GetTransactionsDataUseCase
    @Inject
    constructor(
        private val repository: TransactionsRepository,
    ) {
        /**
         * Executes the use case.
         */
        suspend operator fun invoke(): Result<Transactions> = repository.getTransactionsData()
    }
