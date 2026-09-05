/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform.localization

import app.cash.turbine.test
import com.danhdue.core.pref.CacheStore
import com.danhdue.platform.AppEvent
import com.danhdue.platform.AppEventBus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppLocalizationManagerTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatcherProvider =
        object : com.danhdue.core.coroutines.DispatcherProvider {
            override val main = testDispatcher
            override val default = testDispatcher
            override val io = testDispatcher
            override val unconfined = testDispatcher
        }

    private val cacheStore: CacheStore = mockk(relaxed = true)
    private val appEventBus = AppEventBus()

    private lateinit var localizationManager: AppLocalizationManager

    @Before
    fun setUp() {
        coEvery { cacheStore.read(KEY_APP_LANGUAGE, "en") } returns "en"
    }

    @Test
    fun `default initial language is en`() =
        runTest(testDispatcher) {
            localizationManager =
                DefaultAppLocalizationManager(
                    cacheStore = cacheStore,
                    appEventBus = appEventBus,
                    dispatcherProvider = dispatcherProvider,
                    initialLanguage = "en",
                )

            assertEquals("en", localizationManager.currentLanguageCode.value)
        }

    @Test
    fun `setLocale updates language state, persists to cache, and publishes AppLanguageChanged`() =
        runTest(testDispatcher) {
            localizationManager =
                DefaultAppLocalizationManager(
                    cacheStore = cacheStore,
                    appEventBus = appEventBus,
                    dispatcherProvider = dispatcherProvider,
                    initialLanguage = "en",
                )

            appEventBus.on<AppEvent.AppLanguageChanged>().test {
                localizationManager.setLocale("vi")

                assertEquals("vi", localizationManager.currentLanguageCode.value)

                val event = awaitItem()
                assertEquals("vi", event.languageCode)

                coVerify { cacheStore.write(KEY_APP_LANGUAGE, "vi") }
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `applyDynamicTranslations stores overrides and getString returns dynamic value`() =
        runTest(testDispatcher) {
            localizationManager =
                DefaultAppLocalizationManager(
                    cacheStore = cacheStore,
                    appEventBus = appEventBus,
                    dispatcherProvider = dispatcherProvider,
                    initialLanguage = "en",
                )

            val translations =
                mapOf(
                    "settings.title" to "Cài đặt",
                    "auth.login" to "Đăng nhập",
                )

            localizationManager.applyDynamicTranslations(translations)

            assertEquals("Cài đặt", localizationManager.getString("settings.title", fallback = "Settings"))
            assertEquals("Đăng nhập", localizationManager.getString("auth.login", fallback = "Login"))
            assertEquals("Fallback", localizationManager.getString("non.existing.key", fallback = "Fallback"))
        }

    @Test
    fun `applyDynamicTranslations merges incremental translations`() =
        runTest(testDispatcher) {
            localizationManager =
                DefaultAppLocalizationManager(
                    cacheStore = cacheStore,
                    appEventBus = appEventBus,
                    dispatcherProvider = dispatcherProvider,
                    initialLanguage = "en",
                )

            localizationManager.applyDynamicTranslations(mapOf("key1" to "value1", "key2" to "value2"))
            localizationManager.applyDynamicTranslations(mapOf("key2" to "value2_updated", "key3" to "value3"))

            assertEquals("value1", localizationManager.getString("key1", fallback = ""))
            assertEquals("value2_updated", localizationManager.getString("key2", fallback = ""))
            assertEquals("value3", localizationManager.getString("key3", fallback = ""))
        }

    companion object {
        private const val KEY_APP_LANGUAGE = "key_app_language"
    }
}
