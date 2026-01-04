/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.login.data.repository

import com.danhdue.authentication.login.domain.model.Login
import com.danhdue.authentication.login.domain.repository.LoginRepository
import javax.inject.Inject

/**
 * Concrete implementation of the repository for the Login feature.
 */
class DefaultLoginRepository
    @Inject
    constructor() : LoginRepository {
        override suspend fun getLoginData(): Result<Login> =
            try {
                val domainModel = Login(id = "1", data = "Sample data from repository")
                Result.success(domainModel)
            } catch (e: Exception) {
                Result.failure(e)
            }
    }
