/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.home.domain.repository

import com.danhdue.home.domain.model.Home

/**
 * Interface defining the contract for the Home feature's repository.
 */
interface HomeRepository {
    /**
     * Retrieves data for the Home feature.
     *
     * @return A Result object containing the Home domain model on success,
     * or an exception on failure.
     */
    suspend fun getHomeData(): Result<Home>
}
