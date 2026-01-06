/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.trends.domain.usecase

import com.danhdue.trends.domain.model.Trends
import com.danhdue.trends.domain.repository.TrendsRepository

/**
 * Use case that encapsulates the business logic for fetching the Trends feature data.
 */
class GetTrendsDataUseCase constructor(
    private val repository: TrendsRepository,
) {
    /**
     * Executes the use case.
     */
    suspend operator fun invoke(): Result<Trends> = repository.getTrendsData()
}
