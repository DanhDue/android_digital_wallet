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
                val remoteLanguages = response.data?.availableLanguages.orEmpty()
                val domainLanguages =
                    remoteLanguages.map { dto ->
                        val isBundled = isBundledLanguage(dto.languageCode)
                        SupportedLanguage(
                            code = dto.languageCode,
                            name = dto.languageName,
                            version = dto.version,
                            isDefault = dto.isDefault,
                            isActive = dto.isActive,
                            isCached = isBundled || localDataSource.getTranslations(dto.languageCode).isNotEmpty(),
                        )
                    }
                val merged = mergeWithBundledLanguages(domainLanguages)
                localDataSource.saveSupportedLanguages(merged)
                merged
            }.recoverCatching {
                val fallback = localDataSource.getSupportedLanguages()
                if (fallback.isNotEmpty()) fallback else DEFAULT_BUNDLED_LANGUAGES
            }
        }

    @Suppress("UNCHECKED_CAST")
    override suspend fun fetchAndCacheTranslations(
        languageCode: String,
        sinceVersion: String?,
    ): Result<Map<String, String>> =
        withContext(dispatcherProvider.io) {
            runCatching {
                val responseBody = apiService.getTranslations(languageCode, sinceVersion)
                val rawString = responseBody.string()
                val parsedEnvelope = jsonAdapter.fromJson(rawString).orEmpty()

                val dataMap = parsedEnvelope["data"] as? Map<String, Any?> ?: parsedEnvelope
                val translationsMap = dataMap["translations"] as? Map<String, Any?> ?: dataMap
                val flattened = JsonFlattener.flatten(translationsMap)

                val deletedKeys =
                    (dataMap["deleted_keys"] as? List<*>)?.filterIsInstance<String>().orEmpty()

                val existingTranslations = localDataSource.getTranslations(languageCode)
                if (flattened.isEmpty() && deletedKeys.isEmpty()) {
                    existingTranslations
                } else {
                    val mergedTranslations = existingTranslations.toMutableMap()
                    deletedKeys.forEach { mergedTranslations.remove(it) }
                    mergedTranslations.putAll(flattened)

                    localDataSource.saveTranslations(languageCode, mergedTranslations)
                    localizationManager.applyDynamicTranslations(mergedTranslations, languageCode)

                    val cachedLanguages = localDataSource.getSupportedLanguages()
                    val updatedLanguages =
                        cachedLanguages.map {
                            if (it.code == languageCode) it.copy(isCached = true) else it
                        }
                    localDataSource.saveSupportedLanguages(updatedLanguages)

                    mergedTranslations
                }
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

    private fun isBundledLanguage(code: String): Boolean =
        code in listOf("en", "vi", "en_US", "vi_VN") ||
            code.startsWith("en") ||
            code.startsWith("vi")

    private fun mergeWithBundledLanguages(remoteLanguages: List<SupportedLanguage>): List<SupportedLanguage> {
        val result = mutableListOf<SupportedLanguage>()
        val hasEnglish = remoteLanguages.any { it.code.startsWith("en") }
        val hasVietnamese = remoteLanguages.any { it.code.startsWith("vi") }

        remoteLanguages.forEach { remote ->
            result.add(
                if (isBundledLanguage(remote.code)) {
                    remote.copy(isCached = true)
                } else {
                    remote
                },
            )
        }

        if (!hasEnglish) {
            result.add(0, DEFAULT_BUNDLED_LANGUAGES[0])
        }
        if (!hasVietnamese) {
            val insertIndex = if (result.size >= 1) 1 else 0
            result.add(insertIndex, DEFAULT_BUNDLED_LANGUAGES[1])
        }
        return result
    }

    companion object {
        val DEFAULT_BUNDLED_LANGUAGES =
            listOf(
                SupportedLanguage(
                    code = "en",
                    name = "English",
                    version = "1.0.0",
                    isDefault = true,
                    isActive = true,
                    isCached = true,
                ),
                SupportedLanguage(
                    code = "vi",
                    name = "Tiếng Việt",
                    version = "1.0.0",
                    isDefault = false,
                    isActive = true,
                    isCached = true,
                ),
            )
    }
}
