/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.domain.usecase

import app.cash.turbine.test
import com.danhdue.platform.localization.AppLocalizationManager
import com.danhdue.settings.domain.model.LanguageSyncStatus
import com.danhdue.settings.domain.model.SupportedLanguage
import com.danhdue.settings.domain.repository.SettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ChangeLanguageUseCaseTest {
    private val repository: SettingsRepository = mockk(relaxed = true)
    private val localizationManager: AppLocalizationManager = mockk(relaxed = true)

    private lateinit var useCase: ChangeLanguageUseCase

    @Before
    fun setUp() {
        useCase = ChangeLanguageUseCase(repository, localizationManager)
    }

    @Test
    fun `bundled language emits CachedApplied and Success immediately`() =
        runTest {
            val vietnamese =
                SupportedLanguage(
                    code = "vi",
                    name = "Tiếng Việt",
                    version = "1.0.0",
                    isDefault = false,
                    isCached = true,
                )

            useCase(vietnamese).test {
                val first = awaitItem()
                assertTrue(first is LanguageSyncStatus.CachedApplied)
                assertEquals("vi", (first as LanguageSyncStatus.CachedApplied).languageCode)

                coVerify { localizationManager.setLocale("vi") }

                val second = awaitItem()
                assertTrue(second is LanguageSyncStatus.Success)
                assertEquals("vi", (second as LanguageSyncStatus.Success).languageCode)

                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `cached remote language emits CachedApplied and Success immediately`() =
        runTest {
            val japanese =
                SupportedLanguage(
                    code = "ja_JP",
                    name = "日本語",
                    version = "1.0.0",
                    isCached = true,
                )

            useCase(japanese).test {
                val first = awaitItem()
                assertTrue(first is LanguageSyncStatus.CachedApplied)

                coVerify { localizationManager.setLocale("ja_JP") }

                val second = awaitItem()
                assertTrue(second is LanguageSyncStatus.Success)

                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `uncached language emits Loading, downloads, and emits Success`() =
        runTest {
            val korean =
                SupportedLanguage(
                    code = "ko_KR",
                    name = "한국어",
                    version = "1.0.0",
                    isCached = false,
                )
            coEvery { repository.fetchAndCacheTranslations("ko_KR") } returns Result.success(mapOf("key" to "val"))

            useCase(korean).test {
                val first = awaitItem()
                assertTrue(first is LanguageSyncStatus.Loading)
                assertEquals("ko_KR", (first as LanguageSyncStatus.Loading).languageCode)

                val second = awaitItem()
                assertTrue(second is LanguageSyncStatus.Success)
                assertEquals("ko_KR", (second as LanguageSyncStatus.Success).languageCode)

                coVerify { localizationManager.setLocale("ko_KR") }
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `uncached language emits Loading and then Error on download failure`() =
        runTest {
            val korean =
                SupportedLanguage(
                    code = "ko_KR",
                    name = "한국어",
                    version = "1.0.0",
                    isCached = false,
                )
            coEvery { repository.fetchAndCacheTranslations("ko_KR") } returns Result.failure(RuntimeException("Network error"))

            useCase(korean).test {
                val first = awaitItem()
                assertTrue(first is LanguageSyncStatus.Loading)

                val second = awaitItem()
                assertTrue(second is LanguageSyncStatus.Error)
                assertEquals("ko_KR", (second as LanguageSyncStatus.Error).languageCode)

                coVerify(exactly = 0) { localizationManager.setLocale("ko_KR") }
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `bundled English US language emits CachedApplied and Success immediately`() =
        runTest {
            val englishUs =
                SupportedLanguage(
                    code = "en_US",
                    name = "English (US)",
                    version = "1.0.0",
                    isDefault = true,
                    isCached = true,
                )

            useCase(englishUs).test {
                val first = awaitItem()
                assertTrue(first is LanguageSyncStatus.CachedApplied)
                assertEquals("en_US", (first as LanguageSyncStatus.CachedApplied).languageCode)

                coVerify { localizationManager.setLocale("en_US") }

                val second = awaitItem()
                assertTrue(second is LanguageSyncStatus.Success)
                assertEquals("en_US", (second as LanguageSyncStatus.Success).languageCode)

                cancelAndConsumeRemainingEvents()
            }
        }
}
