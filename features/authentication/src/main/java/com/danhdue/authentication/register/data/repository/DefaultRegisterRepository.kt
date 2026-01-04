/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.register.data.repository

import com.danhdue.authentication.register.domain.model.Register
import com.danhdue.authentication.register.domain.repository.RegisterRepository
import javax.inject.Inject

/**
 * Concrete implementation of the repository for the Register feature.
 */
class DefaultRegisterRepository
    @Inject
    constructor() : RegisterRepository {
        override suspend fun getRegisterData(): Result<Register> =
            try {
                val domainModel = Register(id = "1", data = "Sample data from repository")
                Result.success(domainModel)
            } catch (e: Exception) {
                Result.failure(e)
            }
    }
