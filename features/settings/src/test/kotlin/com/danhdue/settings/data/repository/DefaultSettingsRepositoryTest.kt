/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.data.repository

import com.danhdue.core.coroutines.DispatcherProvider
import com.danhdue.platform.localization.AppLocalizationManager
import com.danhdue.settings.data.local.SettingsLocalDataSource
import com.danhdue.settings.data.remote.SettingsApiService
import com.danhdue.settings.data.remote.dto.BootstrapResponseDto
import com.danhdue.settings.data.remote.dto.SupportedLanguageDto
import com.danhdue.settings.domain.model.SupportedLanguage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
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
    fun `bootstrap fetches remote languages, caches them, and returns success`() =
        runTest(testDispatcher) {
            val dtoList =
                listOf(
                    SupportedLanguageDto(languageCode = "en", languageName = "English", version = "1.0.0", isDefault = true),
                    SupportedLanguageDto(languageCode = "vi", languageName = "Tiếng Việt", version = "1.0.0"),
                    SupportedLanguageDto(languageCode = "ja_JP", languageName = "日本語", version = "1.0.0"),
                )
            coEvery { apiService.bootstrap(any()) } returns
                BootstrapResponseDto(availableLanguages = dtoList, staleTranslations = emptyList())
            coEvery { localDataSource.getSupportedLanguages() } returns emptyList()

            val result = repository.bootstrap()

            assertTrue(result.isSuccess)
            assertEquals(3, result.getOrNull()?.size)
            coVerify { localDataSource.saveSupportedLanguages(any()) }
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
    fun `fetchAndCacheTranslations downloads nested JSON, flattens, caches, and applies overrides`() =
        runTest(testDispatcher) {
            val rawJson =
                """
                {
                    "settings": {
                        "preferences": {
                            "darkMode": "ダークモード"
                        }
                    }
                }
                """.trimIndent()

            coEvery { apiService.getTranslations("ja_JP", any()) } returns rawJson.toResponseBody()

            val result = repository.fetchAndCacheTranslations("ja_JP")

            assertTrue(result.isSuccess)
            val map = result.getOrNull().orEmpty()
            assertEquals("ダークモード", map["settings.preferences.darkMode"])

            coVerify { localDataSource.saveTranslations("ja_JP", map) }
            coVerify { localizationManager.applyDynamicTranslations(map) }
        }
}
