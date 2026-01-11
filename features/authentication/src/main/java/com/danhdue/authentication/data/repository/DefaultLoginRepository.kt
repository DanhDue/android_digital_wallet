/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.data.repository

import com.danhdue.authentication.domain.model.Login
import com.danhdue.authentication.domain.repository.LoginRepository
import com.danhdue.framework.network.DataState
import javax.inject.Inject

/**
 * Concrete implementation of the repository for the Login feature.
 */
class DefaultLoginRepository
    @Inject
    constructor() : LoginRepository {
        override suspend fun getLoginData(): DataState<Login> =
            try {
                val domainModel = Login(id = "1", data = "Sample data from repository")
                DataState.Success(domainModel)
            } catch (e: Exception) {
                DataState.Error(e)
            }
    }
