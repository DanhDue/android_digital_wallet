/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.data.local

import android.content.Context
import com.danhdue.core.pref.CacheStore
import com.danhdue.settings.domain.model.SupportedLanguage
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SettingsLocalDataSource @Inject constructor(
    private val cacheStore: CacheStore,
    @ApplicationContext private val context: Context? = null,
) {
    private val moshi = Moshi.Builder().build()
    private val languageListType =
        Types.newParameterizedType(List::class.java, SupportedLanguageItem::class.java)
    private val languageListAdapter = moshi.adapter<List<SupportedLanguageItem>>(languageListType)

    private val stringMapType =
        Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
    private val stringMapAdapter = moshi.adapter<Map<String, String>>(stringMapType)

    suspend fun getSupportedLanguages(): List<SupportedLanguage> {
        val prefsJson =
            context
                ?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                ?.getString(KEY_SUPPORTED_LANGUAGES, null)
        val json =
            if (!prefsJson.isNullOrBlank()) {
                prefsJson
            } else {
                cacheStore.read(KEY_SUPPORTED_LANGUAGES, "")
            }
        if (json.isBlank()) {
            return defaultLanguages()
        }
        return parseLanguagesJson(json)
    }

    fun getSupportedLanguagesSync(): List<SupportedLanguage> {
        val prefsJson =
            context
                ?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                ?.getString(KEY_SUPPORTED_LANGUAGES, null)
        if (prefsJson.isNullOrBlank()) {
            return defaultLanguages()
        }
        return parseLanguagesJson(prefsJson)
    }

    suspend fun saveSupportedLanguages(languages: List<SupportedLanguage>) {
        val dtos =
            languages.map {
                SupportedLanguageItem(
                    code = it.code,
                    name = it.name,
                    version = it.version,
                    isDefault = it.isDefault,
                    isActive = it.isActive,
                    isCached = it.isCached,
                )
            }
        val json = languageListAdapter.toJson(dtos)
        cacheStore.write(KEY_SUPPORTED_LANGUAGES, json)
        context
            ?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            ?.edit()
            ?.putString(KEY_SUPPORTED_LANGUAGES, json)
            ?.apply()
    }

    private fun parseLanguagesJson(json: String): List<SupportedLanguage> =
        runCatching {
            languageListAdapter.fromJson(json)?.map {
                SupportedLanguage(
                    code = it.code,
                    name = it.name,
                    version = it.version,
                    isDefault = it.isDefault,
                    isActive = it.isActive,
                    isCached = it.isCached,
                )
            } ?: defaultLanguages()
        }.getOrDefault(defaultLanguages())

    suspend fun saveTranslations(
        languageCode: String,
        translations: Map<String, String>,
    ) {
        val json = stringMapAdapter.toJson(translations)
        cacheStore.write(keyForLanguage(languageCode), json)
    }

    suspend fun getTranslations(languageCode: String): Map<String, String> {
        val json = cacheStore.read(keyForLanguage(languageCode), "")
        if (json.isBlank()) return emptyMap()
        return runCatching {
            stringMapAdapter.fromJson(json) ?: emptyMap()
        }.getOrDefault(emptyMap())
    }

    private fun keyForLanguage(languageCode: String) = "translations_$languageCode"

    private fun defaultLanguages(): List<SupportedLanguage> =
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

    companion object {
        const val PREFS_NAME = "app_preferences"
        const val KEY_SUPPORTED_LANGUAGES = "key_supported_languages"
    }
}
