/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.trends.data.repository

import com.danhdue.trends.domain.model.Trends
import com.danhdue.trends.domain.repository.TrendsRepository

/**
 * Concrete implementation of the repository for the Trends feature.
 */
@Suppress("EmptyDefaultConstructor")
class DefaultTrendsRepository constructor() : TrendsRepository {
    override suspend fun getTrendsData(): Result<Trends> =
        try {
            val domainModel = Trends(id = "1", data = "Sample data from repository")
            Result.success(domainModel)
        } catch (e: Exception) {
            Result.failure(e)
        }
}
