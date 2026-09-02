/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.data.repository

import com.danhdue.authentication.domain.entities.Register
import com.danhdue.authentication.domain.repository.RegisterRepository
import com.danhdue.core.network.DataState
import javax.inject.Inject

/**
 * Concrete implementation of the repository for the Register feature.
 */
class DefaultRegisterRepository
    @Inject
    constructor() : RegisterRepository {
        override suspend fun getRegisterData(): DataState<Register> =
            try {
                val domainModel = Register(id = "1", data = "Sample data from repository")
                DataState.Success(domainModel)
            } catch (e: Exception) {
                DataState.Error(e)
            }
    }
