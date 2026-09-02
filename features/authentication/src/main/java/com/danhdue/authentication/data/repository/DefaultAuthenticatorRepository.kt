/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.data.repository

import com.danhdue.authentication.domain.entities.Authenticator
import com.danhdue.authentication.domain.repository.AuthenticatorRepository
import com.danhdue.core.network.DataState
import javax.inject.Inject

/**
 * Concrete implementation of the repository for the Authenticator feature.
 */
class DefaultAuthenticatorRepository
    @Inject
    constructor() : AuthenticatorRepository {
        override suspend fun getAuthenticatorData(): DataState<Authenticator> =
            try {
                val domainModel = Authenticator(id = "1", data = "Sample data from repository")
                DataState.Success(domainModel)
            } catch (e: Exception) {
                DataState.Error(e)
            }
    }
