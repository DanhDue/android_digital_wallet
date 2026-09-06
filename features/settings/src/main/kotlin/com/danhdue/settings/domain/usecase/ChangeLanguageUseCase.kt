/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.domain.usecase

import com.danhdue.platform.localization.AppLocalizationManager
import com.danhdue.settings.domain.model.LanguageSyncStatus
import com.danhdue.settings.domain.model.SupportedLanguage
import com.danhdue.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ChangeLanguageUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val appLocalizationManager: AppLocalizationManager,
) {
    operator fun invoke(targetLanguage: SupportedLanguage): Flow<LanguageSyncStatus> =
        flow {
            val code = targetLanguage.code
            val isBundledOrCached =
                code in BUNDLED_LANGUAGES ||
                    code.startsWith("en") ||
                    code.startsWith("vi") ||
                    targetLanguage.isCached

            if (isBundledOrCached) {
                emit(LanguageSyncStatus.CachedApplied(code))
                val cached = settingsRepository.getCachedTranslations(code)
                if (cached.isNotEmpty()) {
                    appLocalizationManager.applyDynamicTranslations(cached, code)
                }
                appLocalizationManager.setLocale(code)
                // Trigger background delta sync silently if needed
                runCatching {
                    settingsRepository.fetchAndCacheTranslations(code, targetLanguage.version)
                }
                emit(LanguageSyncStatus.Success(code))
            } else {
                emit(LanguageSyncStatus.Loading(code))
                val result = settingsRepository.fetchAndCacheTranslations(code)
                result.fold(
                    onSuccess = {
                        appLocalizationManager.setLocale(code)
                        emit(LanguageSyncStatus.Success(code))
                    },
                    onFailure = { error ->
                        emit(
                            LanguageSyncStatus.Error(
                                languageCode = code,
                                message = error.message ?: "Failed to download translations",
                            ),
                        )
                    },
                )
            }
        }

    companion object {
        private val BUNDLED_LANGUAGES = setOf("en", "vi", "en_US", "vi_VN")
    }
}
