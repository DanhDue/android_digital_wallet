/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.data.repository

import com.danhdue.authentication.domain.model.Authenticator
import com.danhdue.authentication.domain.repository.AuthenticatorRepository
import javax.inject.Inject

/**
 * Concrete implementation of the repository for the Authenticator feature.
 */
class DefaultAuthenticatorRepository
    @Inject
    constructor() : AuthenticatorRepository {
        override suspend fun getAuthenticatorData(): Result<Authenticator> =
            try {
                val domainModel = Authenticator(id = "1", data = "Sample data from repository")
                Result.success(domainModel)
            } catch (e: Exception) {
                Result.failure(e)
            }
    }
