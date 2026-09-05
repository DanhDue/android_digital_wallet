/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.domain.usecase

import com.danhdue.settings.domain.model.SupportedLanguage
import com.danhdue.settings.domain.repository.SettingsRepository
import javax.inject.Inject

class BootstrapSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(): Result<List<SupportedLanguage>> = settingsRepository.bootstrap()
}
