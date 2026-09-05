/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.domain.repository

import com.danhdue.settings.domain.model.Profile
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

    /**
     * Retrieves data for the Profile feature.
     *
     * @return A Result object containing the Profile domain model on success,
     * or an exception on failure.
     */
    suspend fun getProfileData(): Result<Profile>

    /**
     * Synchronizes supported languages and translation metadata with the backend.
     */
    suspend fun bootstrap(): Result<List<com.danhdue.settings.domain.model.SupportedLanguage>>

    /**
     * Downloads and caches translations for [languageCode].
     */
    suspend fun fetchAndCacheTranslations(
        languageCode: String,
        sinceVersion: String? = null,
    ): Result<Map<String, String>>

    /**
     * Returns the list of locally cached or bundled supported languages.
     */
    suspend fun getCachedLanguages(): List<com.danhdue.settings.domain.model.SupportedLanguage>

    /**
     * Returns the cached translation map for [languageCode].
     */
    suspend fun getCachedTranslations(languageCode: String): Map<String, String>
}
