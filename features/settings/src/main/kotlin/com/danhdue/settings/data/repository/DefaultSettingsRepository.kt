/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.data.repository

import com.danhdue.core.coroutines.DispatcherProvider
import com.danhdue.platform.localization.AppLocalizationManager
import com.danhdue.settings.data.local.SettingsLocalDataSource
import com.danhdue.settings.data.remote.SettingsApiService
import com.danhdue.settings.data.remote.dto.BootstrapRequestDto
import com.danhdue.settings.data.remote.dto.CachedTranslationDto
import com.danhdue.settings.data.util.JsonFlattener
import com.danhdue.settings.domain.model.Profile
import com.danhdue.settings.domain.model.Settings
import com.danhdue.settings.domain.model.SupportedLanguage
import com.danhdue.settings.domain.repository.SettingsRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Concrete implementation of the repository for the Settings feature.
 */
internal class DefaultSettingsRepository @Inject constructor(
    private val apiService: SettingsApiService,
    private val localDataSource: SettingsLocalDataSource,
    private val localizationManager: AppLocalizationManager,
    private val dispatcherProvider: DispatcherProvider,
) : SettingsRepository {
    private val moshi = Moshi.Builder().build()
    private val mapType =
        Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)
    private val jsonAdapter = moshi.adapter<Map<String, Any?>>(mapType)

    override suspend fun getSettingsData(): Result<Settings> =
        runCatching {
            Settings(id = "1", data = "Sample data from repository")
        }

    override suspend fun getProfileData(): Result<Profile> =
        runCatching {
            Profile(id = "1", data = "Sample data from repository")
        }

    override suspend fun bootstrap(): Result<List<SupportedLanguage>> =
        withContext(dispatcherProvider.io) {
            runCatching {
                val cachedLanguages = localDataSource.getSupportedLanguages()
                val request =
                    BootstrapRequestDto(
                        cachedTranslations =
                            cachedLanguages.map {
                                CachedTranslationDto(resourceId = it.code, version = it.version)
                            },
                    )
                val response = apiService.bootstrap(request)
                val domainLanguages =
                    response.availableLanguages.map { dto ->
                        SupportedLanguage(
                            code = dto.languageCode,
                            name = dto.languageName,
                            version = dto.version,
                            isDefault = dto.isDefault,
                            isActive = dto.isActive,
                            isCached =
                                dto.languageCode in listOf("en", "vi") || localDataSource.getTranslations(dto.languageCode).isNotEmpty(),
                        )
                    }
                localDataSource.saveSupportedLanguages(domainLanguages)
                domainLanguages
            }.recoverCatching {
                val fallback = localDataSource.getSupportedLanguages()
                if (fallback.isNotEmpty()) fallback else throw it
            }
        }

    override suspend fun fetchAndCacheTranslations(
        languageCode: String,
        sinceVersion: String?,
    ): Result<Map<String, String>> =
        withContext(dispatcherProvider.io) {
            runCatching {
                val responseBody = apiService.getTranslations(languageCode, sinceVersion)
                val rawString = responseBody.string()
                val parsedMap = jsonAdapter.fromJson(rawString).orEmpty()
                val flattened = JsonFlattener.flatten(parsedMap)
                localDataSource.saveTranslations(languageCode, flattened)
                localizationManager.applyDynamicTranslations(flattened)
                flattened
            }
        }

    override suspend fun getCachedLanguages(): List<SupportedLanguage> =
        withContext(dispatcherProvider.io) {
            localDataSource.getSupportedLanguages()
        }

    override suspend fun getCachedTranslations(languageCode: String): Map<String, String> =
        withContext(dispatcherProvider.io) {
            localDataSource.getTranslations(languageCode)
        }
}
