/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.domain.usecase

import com.danhdue.settings.domain.model.Profile
import com.danhdue.settings.domain.repository.SettingsRepository
import javax.inject.Inject

/**
 * Use case that encapsulates the business logic for fetching the Profile feature data.
 */
class GetProfileDataUseCase @Inject constructor(
    private val repository: SettingsRepository,
) {
    /**
     * Executes the use case.
     */
    suspend operator fun invoke(): Result<Profile> = repository.getProfileData()
}
