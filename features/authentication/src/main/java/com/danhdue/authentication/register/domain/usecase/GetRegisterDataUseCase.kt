/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.register.domain.usecase

import com.danhdue.authentication.register.domain.model.Register
import com.danhdue.authentication.register.domain.repository.RegisterRepository
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
        suspend operator fun invoke(): Result<Register> = repository.getRegisterData()
    }
