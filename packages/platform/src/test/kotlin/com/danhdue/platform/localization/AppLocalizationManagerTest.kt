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
        coEvery { cacheStore.read<String>(any(), any()) } answers { secondArg() }
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

    @Test
    fun `applyDynamicTranslations scopes overrides per language and increments version`() =
        runTest(testDispatcher) {
            localizationManager =
                DefaultAppLocalizationManager(
                    cacheStore = cacheStore,
                    appEventBus = appEventBus,
                    dispatcherProvider = dispatcherProvider,
                    initialLanguage = "en",
                )

            val initialVersion = localizationManager.translationsVersion.value

            localizationManager.applyDynamicTranslations(
                translations = mapOf("settings.title" to "設定"),
                languageCode = "ja_JP",
            )

            // Version incremented
            assertEquals(initialVersion + 1, localizationManager.translationsVersion.value)

            // Still in "en", so fallback is used
            assertEquals("Settings", localizationManager.getString("settings.title", fallback = "Settings"))

            // Switch to "ja_JP"
            localizationManager.setLocale("ja_JP")
            assertEquals(initialVersion + 2, localizationManager.translationsVersion.value)
            assertEquals("設定", localizationManager.getString("settings.title", fallback = "Settings"))

            // Switch to "vi" -> falls back to Vietnamese fallback
            localizationManager.setLocale("vi")
            assertEquals("Cài đặt", localizationManager.getString("settings.title", fallback = "Cài đặt"))
        }

    @Test
    fun `getString resolves schema aliases between home nav home and home main title`() =
        runTest(testDispatcher) {
            localizationManager =
                DefaultAppLocalizationManager(
                    cacheStore = cacheStore,
                    appEventBus = appEventBus,
                    dispatcherProvider = dispatcherProvider,
                    initialLanguage = "ja_JP",
                )

            localizationManager.applyDynamicTranslations(
                translations = mapOf("home.main.title" to "ホーム"),
                languageCode = "ja_JP",
            )

            assertEquals("ホーム", localizationManager.getString("home.main.title", fallback = "Home"))
            assertEquals("ホーム", localizationManager.getString("home.nav.home", fallback = "Home"))
        }

    @Test
    fun `applyDynamicTranslations with unchanged translations does not increment version`() =
        runTest(testDispatcher) {
            localizationManager =
                DefaultAppLocalizationManager(
                    cacheStore = cacheStore,
                    appEventBus = appEventBus,
                    dispatcherProvider = dispatcherProvider,
                    initialLanguage = "en",
                )

            val initialVersion = localizationManager.translationsVersion.value
            localizationManager.applyDynamicTranslations(mapOf("key1" to "value1"))
            assertEquals(initialVersion + 1, localizationManager.translationsVersion.value)

            // Re-applying exact same translations should NOT increment version
            localizationManager.applyDynamicTranslations(mapOf("key1" to "value1"))
            assertEquals(initialVersion + 1, localizationManager.translationsVersion.value)
        }

    @Test
    fun `startup cache load restores translations into dynamicOverrides`() =
        runTest(testDispatcher) {
            coEvery { cacheStore.read<String>("translations_ja_JP", any()) } returns """{"custom.title":"タイトル"}"""

            localizationManager =
                DefaultAppLocalizationManager(
                    cacheStore = cacheStore,
                    appEventBus = appEventBus,
                    dispatcherProvider = dispatcherProvider,
                    initialLanguage = "ja_JP",
                )

            assertEquals("タイトル", localizationManager.getString("custom.title", fallback = "Default"))
        }

    companion object {
        private const val KEY_APP_LANGUAGE = "key_app_language"
    }
}
