/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.home.data.repository

import com.danhdue.home.domain.model.Home
import com.danhdue.home.domain.repository.HomeRepository
import javax.inject.Inject

/**
 * Concrete implementation of the repository for the Home feature.
 */
class DefaultHomeRepository @Inject constructor() : HomeRepository {
    override suspend fun getHomeData(): Result<Home> =
        try {
            val domainModel = Home(id = "1", data = "Sample data from repository")
            Result.success(domainModel)
        } catch (e: Exception) {
            Result.failure(e)
        }
}
