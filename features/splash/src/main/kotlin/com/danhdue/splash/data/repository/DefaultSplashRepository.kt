/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.splash.data.repository

import com.danhdue.splash.domain.model.Splash
import com.danhdue.splash.domain.repository.SplashRepository
import javax.inject.Inject

/**
 * Concrete implementation of the repository for the Splash feature.
 */
class DefaultSplashRepository
    @Inject
    constructor() : SplashRepository {
        override suspend fun getSplashData(): Result<Splash> =
            try {
                val domainModel = Splash(id = "1", data = "Sample data from repository")
                Result.success(domainModel)
            } catch (e: Exception) {
                Result.failure(e)
            }
    }
