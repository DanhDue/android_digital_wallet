/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.domain.repository

import com.danhdue.settings.domain.model.Settings

/**
 * Interface defining the contract for the Settings feature's repository.
 */
interface SettingsRepository {
    /**
     * Retrieves data for the Settings feature.
     *
     * @return A Result object containing the Settings domain model on success,
     * or an exception on failure.
     */
    suspend fun getSettingsData(): Result<Settings>
}
