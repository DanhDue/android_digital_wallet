/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.data.repository

import com.danhdue.core.coroutines.DispatcherProvider
import com.danhdue.platform.localization.AppLocalizationManager
import com.danhdue.settings.data.local.SettingsLocalDataSource
import com.danhdue.settings.data.remote.SettingsApiService
import com.danhdue.settings.data.remote.dto.BootstrapDataDto
import com.danhdue.settings.data.remote.dto.BootstrapResponseDto
import com.danhdue.settings.data.remote.dto.SupportedLanguageDto
import com.danhdue.settings.domain.model.SupportedLanguage
import com.squareup.moshi.Moshi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultSettingsRepositoryTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatcherProvider =
        object : DispatcherProvider {
            override val main = testDispatcher
            override val default = testDispatcher
            override val io = testDispatcher
            override val unconfined = testDispatcher
        }

    private val apiService: SettingsApiService = mockk()
    private val localDataSource: SettingsLocalDataSource = mockk(relaxed = true)
    private val localizationManager: AppLocalizationManager = mockk(relaxed = true)

    private lateinit var repository: DefaultSettingsRepository

    @Before
    fun setUp() {
        repository =
            DefaultSettingsRepository(
                apiService = apiService,
                localDataSource = localDataSource,
                localizationManager = localizationManager,
                dispatcherProvider = dispatcherProvider,
            )
    }

    @Test
    fun `bootstrap fetches remote languages, ensures bundled defaults, caches and returns list`() =
        runTest(testDispatcher) {
            val dtoList =
                listOf(
                    SupportedLanguageDto(languageCode = "en_US", languageName = "English (US)", version = "1.0.0"),
                    SupportedLanguageDto(languageCode = "ja_JP", languageName = "日本語", version = "1.0.0"),
                    SupportedLanguageDto(languageCode = "ko_KR", languageName = "한국어", version = "1.0.5"),
                )
            val response =
                BootstrapResponseDto(
                    success = true,
                    data = BootstrapDataDto(availableLanguages = dtoList, staleTranslations = emptyList()),
                )
            coEvery { apiService.bootstrap(any()) } returns response
            coEvery { localDataSource.getSupportedLanguages() } returns emptyList()

            val result = repository.bootstrap()

            assertTrue(result.isSuccess)
            val languages = result.getOrNull().orEmpty()
            // Should contain en_US, ja_JP, ko_KR AND guaranteed bundled Vietnamese (vi)
            assertEquals(4, languages.size)
            assertTrue(languages.any { it.code == "en_US" && it.isCached })
            assertTrue(languages.any { it.code == "vi" && it.isCached })
            assertTrue(languages.any { it.code == "ja_JP" && !it.isCached })
            assertTrue(languages.any { it.code == "ko_KR" && !it.isCached })
            coVerify { localDataSource.saveSupportedLanguages(any()) }
        }

    @Test
    fun `bootstrap deserializes live BE JSON response envelope properly`() =
        runTest(testDispatcher) {
            val liveBeJson =
                """
                {
                  "success": true,
                  "message": "Bootstrap successful",
                  "data": {
                    "stale_translations": [],
                    "available_languages": [
                      {"language_code": "en_US", "language_name": "English (US)", "version": "1.0.0", "is_default": false},
                      {"language_code": "ja_JP", "language_name": "日本語", "version": "1.0.0", "is_default": false},
                      {"language_code": "ko_KR", "language_name": "한국어", "version": "1.0.5", "is_default": false}
                    ]
                  }
                }
                """.trimIndent()

            val moshi = Moshi.Builder().build()
            val adapter = moshi.adapter(BootstrapResponseDto::class.java)
            val parsedDto = adapter.fromJson(liveBeJson)

            assertNotNull(parsedDto)
            assertEquals(3, parsedDto?.data?.availableLanguages?.size)

            coEvery { apiService.bootstrap(any()) } returns parsedDto!!
            coEvery { localDataSource.getSupportedLanguages() } returns emptyList()

            val result = repository.bootstrap()

            assertTrue(result.isSuccess)
            val languages = result.getOrNull().orEmpty()
            assertEquals(4, languages.size)
        }

    @Test
    fun `bootstrap returns cached languages when remote call fails`() =
        runTest(testDispatcher) {
            coEvery { apiService.bootstrap(any()) } throws IOException("Network timeout")
            val cachedList =
                listOf(
                    SupportedLanguage(code = "en", name = "English", version = "1.0.0"),
                    SupportedLanguage(code = "vi", name = "Tiếng Việt", version = "1.0.0"),
                )
            coEvery { localDataSource.getSupportedLanguages() } returns cachedList

            val result = repository.bootstrap()

            assertTrue(result.isSuccess)
            assertEquals(2, result.getOrNull()?.size)
        }

    @Test
    fun `bootstrap returns default bundled languages when both remote and local cache fail`() =
        runTest(testDispatcher) {
            coEvery { apiService.bootstrap(any()) } throws IOException("Network down")
            coEvery { localDataSource.getSupportedLanguages() } returns emptyList()

            val result = repository.bootstrap()

            assertTrue(result.isSuccess)
            val languages = result.getOrNull().orEmpty()
            assertEquals(2, languages.size)
            assertEquals("en", languages[0].code)
            assertEquals("vi", languages[1].code)
        }

    @Test
    fun `fetchAndCacheTranslations unwraps BE data envelope and removes deleted keys`() =
        runTest(testDispatcher) {
            val liveTranslationJson =
                """
                {
                  "success": true,
                  "message": "Translation retrieved",
                  "data": {
                    "language_code": "ja_JP",
                    "translations": {
                      "settings": {
                        "preferences": {
                          "darkMode": "ダークモード"
                        }
                      }
                    },
                    "deleted_keys": ["old_key"]
                  }
                }
                """.trimIndent()

            coEvery { localDataSource.getTranslations("ja_JP") } returns mapOf("old_key" to "Old Value")
            coEvery { apiService.getTranslations("ja_JP", any()) } returns liveTranslationJson.toResponseBody()

            val result = repository.fetchAndCacheTranslations("ja_JP")

            assertTrue(result.isSuccess)
            val map = result.getOrNull().orEmpty()
            assertEquals("ダークモード", map["settings.preferences.darkMode"])
            assertTrue(!map.containsKey("old_key"))

            coVerify { localDataSource.saveTranslations("ja_JP", map) }
            coVerify { localizationManager.applyDynamicTranslations(map, "ja_JP") }
        }
}
