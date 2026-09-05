/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform.theme

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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppThemeManagerTest {
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

    private lateinit var themeManager: AppThemeManager

    @Before
    fun setUp() {
        coEvery { cacheStore.read(KEY_THEME_MODE, AppThemeMode.SYSTEM.name) } returns AppThemeMode.SYSTEM.name
    }

    @Test
    fun `default initial theme is SYSTEM and reflects system dark mode`() =
        runTest(testDispatcher) {
            themeManager =
                DefaultAppThemeManager(
                    cacheStore = cacheStore,
                    appEventBus = appEventBus,
                    dispatcherProvider = dispatcherProvider,
                    isSystemDarkInitial = false,
                )

            assertEquals(AppThemeMode.SYSTEM, themeManager.themeMode.value)
            assertFalse(themeManager.isDarkMode.value)
        }

    @Test
    fun `initial theme with stored DARK sets isDarkMode to true`() =
        runTest(testDispatcher) {
            coEvery { cacheStore.read(KEY_THEME_MODE, AppThemeMode.SYSTEM.name) } returns AppThemeMode.DARK.name

            themeManager =
                DefaultAppThemeManager(
                    cacheStore = cacheStore,
                    appEventBus = appEventBus,
                    dispatcherProvider = dispatcherProvider,
                    isSystemDarkInitial = false,
                )

            assertEquals(AppThemeMode.DARK, themeManager.themeMode.value)
            assertTrue(themeManager.isDarkMode.value)
        }

    @Test
    fun `setThemeMode DARK updates state, persists to cache, and publishes ThemeModeChanged`() =
        runTest(testDispatcher) {
            themeManager =
                DefaultAppThemeManager(
                    cacheStore = cacheStore,
                    appEventBus = appEventBus,
                    dispatcherProvider = dispatcherProvider,
                    isSystemDarkInitial = false,
                )

            appEventBus.on<AppEvent.ThemeModeChanged>().test {
                themeManager.setThemeMode(AppThemeMode.DARK)

                assertEquals(AppThemeMode.DARK, themeManager.themeMode.value)
                assertTrue(themeManager.isDarkMode.value)

                val event = awaitItem()
                assertTrue(event.isDarkMode)

                coVerify { cacheStore.write(KEY_THEME_MODE, AppThemeMode.DARK.name) }
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `setThemeMode LIGHT updates state, persists to cache, and publishes ThemeModeChanged false`() =
        runTest(testDispatcher) {
            themeManager =
                DefaultAppThemeManager(
                    cacheStore = cacheStore,
                    appEventBus = appEventBus,
                    dispatcherProvider = dispatcherProvider,
                    isSystemDarkInitial = true,
                )

            appEventBus.on<AppEvent.ThemeModeChanged>().test {
                themeManager.setThemeMode(AppThemeMode.LIGHT)

                assertEquals(AppThemeMode.LIGHT, themeManager.themeMode.value)
                assertFalse(themeManager.isDarkMode.value)

                val event = awaitItem()
                assertFalse(event.isDarkMode)

                coVerify { cacheStore.write(KEY_THEME_MODE, AppThemeMode.LIGHT.name) }
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `rapid theme switching race condition results in final theme applied`() =
        runTest(testDispatcher) {
            themeManager =
                DefaultAppThemeManager(
                    cacheStore = cacheStore,
                    appEventBus = appEventBus,
                    dispatcherProvider = dispatcherProvider,
                    isSystemDarkInitial = false,
                )

            themeManager.setThemeMode(AppThemeMode.DARK)
            themeManager.setThemeMode(AppThemeMode.LIGHT)
            themeManager.setThemeMode(AppThemeMode.DARK)

            assertEquals(AppThemeMode.DARK, themeManager.themeMode.value)
            assertTrue(themeManager.isDarkMode.value)
        }

    companion object {
        private const val KEY_THEME_MODE = "key_app_theme_mode"
    }
}
