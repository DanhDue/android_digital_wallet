/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.domain.usecase

import com.danhdue.authentication.domain.model.Login
import com.danhdue.authentication.domain.repository.LoginRepository
import com.danhdue.framework.network.DataState
import javax.inject.Inject

/**
 * Use case that encapsulates the business logic for fetching the Login feature data.
 */
class GetLoginDataUseCase
    @Inject
    constructor(
        private val repository: LoginRepository,
    ) {
        /**
         * Executes the use case.
         */
        suspend operator fun invoke(): DataState<Login> = repository.getLoginData()
    }
