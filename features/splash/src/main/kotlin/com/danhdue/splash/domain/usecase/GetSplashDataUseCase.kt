/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.splash.domain.usecase

import com.danhdue.splash.domain.model.Splash
import com.danhdue.splash.domain.repository.SplashRepository
import javax.inject.Inject

/**
 * Use case that encapsulates the business logic for fetching the Splash feature data.
 */
class GetSplashDataUseCase
    @Inject
    constructor(
        private val repository: SplashRepository,
    ) {
        /**
         * Executes the use case.
         */
        suspend operator fun invoke(): Result<Splash> = repository.getSplashData()
    }
