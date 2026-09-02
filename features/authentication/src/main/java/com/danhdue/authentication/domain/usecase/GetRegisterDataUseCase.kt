/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.domain.usecase

import com.danhdue.authentication.domain.entities.Register
import com.danhdue.authentication.domain.repository.RegisterRepository
import com.danhdue.core.network.DataState
import javax.inject.Inject

/**
 * Use case that encapsulates the business logic for fetching the Register feature data.
 */
class GetRegisterDataUseCase
    @Inject
    constructor(
        private val repository: RegisterRepository,
    ) {
        /**
         * Executes the use case.
         */
        suspend operator fun invoke(): DataState<Register> = repository.getRegisterData()
    }
