/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.home.domain.usecase

import com.danhdue.home.domain.model.Home
import com.danhdue.home.domain.repository.HomeRepository
import javax.inject.Inject

/**
 * Use case that encapsulates the business logic for fetching the Home feature data.
 */
class GetHomeDataUseCase @Inject constructor(
    private val repository: HomeRepository,
) {
    /**
     * Executes the use case.
     */
    suspend operator fun invoke(): Result<Home> = repository.getHomeData()
}
