/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.login.domain.usecase

import com.danhdue.authentication.login.domain.model.Login
import com.danhdue.authentication.login.domain.repository.LoginRepository
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
        suspend operator fun invoke(): Result<Login> = repository.getLoginData()
    }
