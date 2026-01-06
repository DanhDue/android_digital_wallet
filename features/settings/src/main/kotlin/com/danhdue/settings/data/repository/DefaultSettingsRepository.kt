/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.data.repository

import com.danhdue.settings.domain.model.Settings
import com.danhdue.settings.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Concrete implementation of the repository for the Settings feature.
 */
class DefaultSettingsRepository
    @Inject
    constructor() : SettingsRepository {
        override suspend fun getSettingsData(): Result<Settings> =
            try {
                val domainModel = Settings(id = "1", data = "Sample data from repository")
                Result.success(domainModel)
            } catch (e: Exception) {
                Result.failure(e)
            }
    }
