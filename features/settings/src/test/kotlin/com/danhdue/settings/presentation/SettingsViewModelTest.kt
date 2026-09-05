/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.presentation

import app.cash.turbine.test
import com.danhdue.libraries.testutils.TestCoroutineRule
import com.danhdue.platform.AppEventBus
import com.danhdue.platform.localization.AppLocalizationManager
import com.danhdue.platform.theme.AppThemeManager
import com.danhdue.platform.theme.AppThemeMode
import com.danhdue.settings.domain.model.LanguageSyncStatus
import com.danhdue.settings.domain.model.Profile
import com.danhdue.settings.domain.model.Settings
import com.danhdue.settings.domain.model.SupportedLanguage
import com.danhdue.settings.domain.repository.SettingsRepository
import com.danhdue.settings.domain.usecase.BootstrapSettingsUseCase
import com.danhdue.settings.domain.usecase.ChangeLanguageUseCase
import com.danhdue.settings.domain.usecase.GetProfileDataUseCase
import com.danhdue.settings.domain.usecase.GetSettingsDataUseCase
import com.danhdue.settings.domain.usecase.ToggleDarkModeUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {
    @get:Rule
    val coroutineRule = TestCoroutineRule()

    private val repository: SettingsRepository = mockk(relaxed = true)
    private val appThemeManager: AppThemeManager = mockk(relaxed = true)
    private val appLocalizationManager: AppLocalizationManager = mockk(relaxed = true)
    private val appEventBus = AppEventBus()

    private val isDarkModeFlow = MutableStateFlow(false)
    private val themeModeFlow = MutableStateFlow(AppThemeMode.SYSTEM)
    private val currentLanguageCodeFlow = MutableStateFlow("en")

    private val defaultLanguages =
        listOf(
            SupportedLanguage(code = "en", name = "English", version = "1.0.0", isDefault = true),
            SupportedLanguage(code = "vi", name = "Tiếng Việt", version = "1.0.0"),
            SupportedLanguage(code = "ja_JP", name = "日本語", version = "1.0.0"),
        )

    @Before
    fun setUp() {
        every { appThemeManager.isDarkMode } returns isDarkModeFlow
        every { appThemeManager.themeMode } returns themeModeFlow
        every { appLocalizationManager.currentLanguageCode } returns currentLanguageCodeFlow

        coEvery { repository.getSettingsData() } returns Result.success(Settings(id = "1", data = "ok"))
        coEvery { repository.getProfileData() } returns Result.success(Profile(id = "1", data = "Ada Lovelace"))
        coEvery { repository.bootstrap() } returns Result.success(defaultLanguages)
        coEvery { repository.getCachedLanguages() } returns defaultLanguages
    }

    private fun createViewModel(): SettingsViewModel =
        SettingsViewModel(
            getSettingsDataUseCase = GetSettingsDataUseCase(repository),
            getProfileDataUseCase = GetProfileDataUseCase(repository),
            bootstrapSettingsUseCase = BootstrapSettingsUseCase(repository),
            changeLanguageUseCase = ChangeLanguageUseCase(repository, appLocalizationManager),
            toggleDarkModeUseCase = ToggleDarkModeUseCase(appThemeManager),
            appThemeManager = appThemeManager,
            appLocalizationManager = appLocalizationManager,
            appEventBus = appEventBus,
        )

    @Test
    fun `initial state reflects theme and localization managers and loads languages`() =
        coroutineRule.runTest {
            val viewModel = createViewModel()

            assertFalse(viewModel.uiState.value.isDarkMode)
            assertEquals("en", viewModel.uiState.value.selectedLanguageCode)
            assertEquals(
                3,
                viewModel
                    .uiState
                    .value
                    .availableLanguages
                    .size,
            )
        }

    @Test
    fun `ToggleDarkMode action calls toggleDarkModeUseCase and updates theme`() =
        coroutineRule.runTest {
            val viewModel = createViewModel()

            viewModel.dispatch(SettingsAction.ToggleDarkMode(true))

            coVerify { appThemeManager.setThemeMode(AppThemeMode.DARK) }
        }

    @Test
    fun `OpenLanguagePicker and DismissLanguagePicker toggles picker visibility`() =
        coroutineRule.runTest {
            val viewModel = createViewModel()

            viewModel.dispatch(SettingsAction.OpenLanguagePicker)
            assertTrue(viewModel.uiState.value.isLanguagePickerVisible)

            viewModel.dispatch(SettingsAction.DismissLanguagePicker)
            assertFalse(viewModel.uiState.value.isLanguagePickerVisible)
        }

    @Test
    fun `SelectLanguage with cached language switches language and closes picker`() =
        coroutineRule.runTest {
            val viewModel = createViewModel()
            val vi = defaultLanguages[1]

            viewModel.dispatch(SettingsAction.OpenLanguagePicker)
            viewModel.dispatch(SettingsAction.SelectLanguage(vi))

            assertFalse(viewModel.uiState.value.isLanguagePickerVisible)
            assertEquals("vi", viewModel.uiState.value.selectedLanguageCode)
        }

    @Test
    fun `SelectLanguage with uncached language triggers loading dialog`() =
        coroutineRule.runTest {
            val mockChangeLanguageUseCase: ChangeLanguageUseCase = mockk()
            val ko = SupportedLanguage(code = "ko_KR", name = "한국어", version = "1.0.0", isCached = false)

            every { mockChangeLanguageUseCase(ko) } returns
                flowOf(
                    LanguageSyncStatus.Loading("ko_KR"),
                    LanguageSyncStatus.Success("ko_KR"),
                )

            val viewModel =
                SettingsViewModel(
                    getSettingsDataUseCase = GetSettingsDataUseCase(repository),
                    getProfileDataUseCase = GetProfileDataUseCase(repository),
                    bootstrapSettingsUseCase = BootstrapSettingsUseCase(repository),
                    changeLanguageUseCase = mockChangeLanguageUseCase,
                    toggleDarkModeUseCase = ToggleDarkModeUseCase(appThemeManager),
                    appThemeManager = appThemeManager,
                    appLocalizationManager = appLocalizationManager,
                    appEventBus = appEventBus,
                )

            viewModel.dispatch(SettingsAction.SelectLanguage(ko))

            assertFalse(viewModel.uiState.value.isLoadingLanguage)
            assertEquals("ko_KR", viewModel.uiState.value.selectedLanguageCode)
        }

    @Test
    fun `SelectLanguage handles error and emits ShowToast event`() =
        coroutineRule.runTest {
            val mockChangeLanguageUseCase: ChangeLanguageUseCase = mockk()
            val ko = SupportedLanguage(code = "ko_KR", name = "한국어", version = "1.0.0", isCached = false)

            every { mockChangeLanguageUseCase(ko) } returns
                flowOf(
                    LanguageSyncStatus.Loading("ko_KR"),
                    LanguageSyncStatus.Error("ko_KR", "Network timeout"),
                )

            val viewModel =
                SettingsViewModel(
                    getSettingsDataUseCase = GetSettingsDataUseCase(repository),
                    getProfileDataUseCase = GetProfileDataUseCase(repository),
                    bootstrapSettingsUseCase = BootstrapSettingsUseCase(repository),
                    changeLanguageUseCase = mockChangeLanguageUseCase,
                    toggleDarkModeUseCase = ToggleDarkModeUseCase(appThemeManager),
                    appThemeManager = appThemeManager,
                    appLocalizationManager = appLocalizationManager,
                    appEventBus = appEventBus,
                )

            viewModel.events.test {
                viewModel.dispatch(SettingsAction.SelectLanguage(ko))

                val event = awaitItem()
                assertTrue(event is SettingsEvent.ShowToast)
                assertEquals("Network timeout", (event as SettingsEvent.ShowToast).message)

                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `rapid SelectLanguage cancels previous in-flight job and applies latest`() =
        coroutineRule.runTest {
            val mockChangeLanguageUseCase: ChangeLanguageUseCase = mockk()
            val ko = SupportedLanguage(code = "ko_KR", name = "한국어", version = "1.0.0", isCached = false)
            val ja = SupportedLanguage(code = "ja_JP", name = "日本語", version = "1.0.0", isCached = false)

            every { mockChangeLanguageUseCase(ko) } returns
                flow {
                    emit(LanguageSyncStatus.Loading("ko_KR"))
                    kotlinx.coroutines.delay(1000)
                    emit(LanguageSyncStatus.Success("ko_KR"))
                }
            every { mockChangeLanguageUseCase(ja) } returns
                flowOf(
                    LanguageSyncStatus.Loading("ja_JP"),
                    LanguageSyncStatus.Success("ja_JP"),
                )

            val viewModel =
                SettingsViewModel(
                    getSettingsDataUseCase = GetSettingsDataUseCase(repository),
                    getProfileDataUseCase = GetProfileDataUseCase(repository),
                    bootstrapSettingsUseCase = BootstrapSettingsUseCase(repository),
                    changeLanguageUseCase = mockChangeLanguageUseCase,
                    toggleDarkModeUseCase = ToggleDarkModeUseCase(appThemeManager),
                    appThemeManager = appThemeManager,
                    appLocalizationManager = appLocalizationManager,
                    appEventBus = appEventBus,
                )

            viewModel.dispatch(SettingsAction.SelectLanguage(ko))
            viewModel.dispatch(SettingsAction.SelectLanguage(ja))

            assertEquals("ja_JP", viewModel.uiState.value.selectedLanguageCode)
            assertEquals("日本語", viewModel.uiState.value.selectedLanguageName)
            assertFalse(viewModel.uiState.value.isLoadingLanguage)
        }
}
