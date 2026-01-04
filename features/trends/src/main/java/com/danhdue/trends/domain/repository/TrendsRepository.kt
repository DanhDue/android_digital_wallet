/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.trends.domain.repository

import com.danhdue.trends.domain.model.Trends

/**
 * Interface defining the contract for the Trends feature's repository.
 */
interface TrendsRepository {
    /**
     * Retrieves data for the Trends feature.
     *
     * @return A Result object containing the Trends domain model on success,
     * or an exception on failure.
     */
    suspend fun getTrendsData(): Result<Trends>
}
