/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.presentation

import androidx.lifecycle.viewModelScope
import com.danhdue.framework.base.mvi.MviViewModel
import com.danhdue.platform.AppEvent
import com.danhdue.platform.AppEventBus
import com.danhdue.platform.localization.AppLocalizationManager
import com.danhdue.platform.theme.AppThemeManager
import com.danhdue.settings.domain.model.LanguageSyncStatus
import com.danhdue.settings.domain.model.SupportedLanguage
import com.danhdue.settings.domain.usecase.BootstrapSettingsUseCase
import com.danhdue.settings.domain.usecase.ChangeLanguageUseCase
import com.danhdue.settings.domain.usecase.GetProfileDataUseCase
import com.danhdue.settings.domain.usecase.GetSettingsDataUseCase
import com.danhdue.settings.domain.usecase.ToggleDarkModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Manages the business logic and state for the Settings feature.
 *
 * MVI: extends [MviViewModel]; UI intents arrive through [onAction], state is
 * mutated with [reduce], one-off navigation events go out via [sendEvent].
 */
@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val getSettingsDataUseCase: GetSettingsDataUseCase,
        private val getProfileDataUseCase: GetProfileDataUseCase,
        private val bootstrapSettingsUseCase: BootstrapSettingsUseCase,
        private val changeLanguageUseCase: ChangeLanguageUseCase,
        private val toggleDarkModeUseCase: ToggleDarkModeUseCase,
        private val appThemeManager: AppThemeManager,
        private val appLocalizationManager: AppLocalizationManager,
        private val appEventBus: AppEventBus,
    ) : MviViewModel<SettingsState, SettingsAction, SettingsEvent>(
            initialState = SettingsState(),
        ) {
        val events: Flow<SettingsEvent> get() = event

        private var languageSyncJob: Job? = null

        init {
            observeThemeAndLocalization()
            loadInitialData()
        }

        override fun onAction(action: SettingsAction) {
            when (action) {
                is SettingsAction.OpenProfile -> sendEvent(SettingsEvent.NavigateToProfile)
                is SettingsAction.OpenSecurity -> sendEvent(SettingsEvent.NavigateToSecurity)
                is SettingsAction.OpenDeveloperOptions -> sendEvent(SettingsEvent.NavigateToDeveloperOptions)
                is SettingsAction.Logout -> {
                    // Handled if logout logic is required
                }
                is SettingsAction.ToggleDarkMode -> {
                    safeLaunch {
                        toggleDarkModeUseCase(action.isDarkMode)
                    }
                }
                is SettingsAction.OpenLanguagePicker -> {
                    reduce { copy(isLanguagePickerVisible = true) }
                }
                is SettingsAction.DismissLanguagePicker -> {
                    reduce { copy(isLanguagePickerVisible = false) }
                }
                is SettingsAction.SelectLanguage -> {
                    handleSelectLanguage(action.language)
                }
            }
        }

        private fun observeThemeAndLocalization() {
            safeLaunch {
                appThemeManager.isDarkMode.collect { isDark ->
                    reduce { copy(isDarkMode = isDark) }
                }
            }
            safeLaunch {
                appLocalizationManager.currentLanguageCode.collect { code ->
                    val langName =
                        currentState.availableLanguages.find { it.code == code }?.name
                            ?: if (code == "vi") "Tiếng Việt" else "English"
                    reduce {
                        copy(
                            selectedLanguageCode = code,
                            selectedLanguageName = langName,
                        )
                    }
                }
            }
        }

        private fun loadInitialData() {
            safeLaunch {
                reduce { copy(isLoading = true) }

                getSettingsDataUseCase()
                    .onSuccess { }
                    .onFailure { }

                getProfileDataUseCase()
                    .onSuccess { profile ->
                        appEventBus.publish(AppEvent.ProfileNameChanged(displayName = profile.data))
                    }.onFailure { }

                bootstrapSettingsUseCase()
                    .onSuccess { languages ->
                        val currentCode = appLocalizationManager.currentLanguageCode.value
                        val langName =
                            languages.find { it.code == currentCode }?.name
                                ?: if (currentCode == "vi") "Tiếng Việt" else "English"
                        reduce {
                            copy(
                                availableLanguages = languages,
                                selectedLanguageCode = currentCode,
                                selectedLanguageName = langName,
                            )
                        }
                    }.onFailure { }

                reduce { copy(isLoading = false) }
            }
        }

        private fun handleSelectLanguage(language: SupportedLanguage) {
            reduce { copy(isLanguagePickerVisible = false) }

            if (language.code == currentState.selectedLanguageCode) {
                return
            }

            languageSyncJob?.cancel()
            languageSyncJob =
                viewModelScope.launch {
                    changeLanguageUseCase(language).collect { status ->
                        when (status) {
                            is LanguageSyncStatus.Idle -> Unit
                            is LanguageSyncStatus.Loading -> {
                                reduce { copy(isLoadingLanguage = true) }
                            }
                            is LanguageSyncStatus.CachedApplied -> {
                                reduce {
                                    copy(
                                        selectedLanguageCode = status.languageCode,
                                        selectedLanguageName = language.name,
                                    )
                                }
                            }
                            is LanguageSyncStatus.Success -> {
                                reduce {
                                    copy(
                                        isLoadingLanguage = false,
                                        selectedLanguageCode = status.languageCode,
                                        selectedLanguageName = language.name,
                                    )
                                }
                            }
                            is LanguageSyncStatus.Error -> {
                                reduce { copy(isLoadingLanguage = false) }
                                sendEvent(SettingsEvent.ShowToast(status.message))
                            }
                        }
                    }
                }
        }
    }
