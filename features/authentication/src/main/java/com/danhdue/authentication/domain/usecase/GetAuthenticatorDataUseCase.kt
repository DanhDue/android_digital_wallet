/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.domain.usecase

import com.danhdue.authentication.domain.model.Authenticator
import com.danhdue.authentication.domain.repository.AuthenticatorRepository
import com.danhdue.framework.network.DataState
import javax.inject.Inject

/**
 * Use case that encapsulates the business logic for fetching the Authenticator feature data.
 */
class GetAuthenticatorDataUseCase
    @Inject
    constructor(
        private val repository: AuthenticatorRepository,
    ) {
        /**
         * Executes the use case.
         */
        suspend operator fun invoke(): DataState<Authenticator> = repository.getAuthenticatorData()
    }
