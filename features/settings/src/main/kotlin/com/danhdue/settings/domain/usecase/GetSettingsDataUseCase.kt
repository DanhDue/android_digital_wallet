/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.domain.usecase

import com.danhdue.settings.domain.model.Settings
import com.danhdue.settings.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Use case that encapsulates the business logic for fetching the Settings feature data.
 */
class GetSettingsDataUseCase
    @Inject
    constructor(
        private val repository: SettingsRepository,
    ) {
        /**
         * Executes the use case.
         */
        suspend operator fun invoke(): Result<Settings> = repository.getSettingsData()
    }
