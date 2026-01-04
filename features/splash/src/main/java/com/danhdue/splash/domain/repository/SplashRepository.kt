/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.splash.domain.repository

import com.danhdue.splash.domain.model.Splash

/**
 * Interface defining the contract for the Splash feature's repository.
 */
interface SplashRepository {
    /**
     * Retrieves data for the Splash feature.
     *
     * @return A Result object containing the Splash domain model on success,
     * or an exception on failure.
     */
    suspend fun getSplashData(): Result<Splash>
}
