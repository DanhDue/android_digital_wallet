/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.presentation

import com.danhdue.settings.domain.model.SupportedLanguage

/**
 * Represents the state of the Settings screen.
 *
 * @property isLoading True if data is currently being loaded.
 * @property isDarkMode True if dark mode is active.
 * @property selectedLanguageCode Currently active language code (e.g., "en", "vi").
 * @property selectedLanguageName Human-readable display name of the selected language.
 * @property availableLanguages List of supported languages available for selection.
 * @property isLanguagePickerVisible Whether the language selection bottom sheet is shown.
 * @property isLoadingLanguage Whether an uncached language translation is downloading.
 * @property errorMessage Error message if an operation failed.
 */
data class SettingsState(
    val isLoading: Boolean = false,
    val isDarkMode: Boolean = false,
    val selectedLanguageCode: String = "en",
    val selectedLanguageName: String = "English",
    val availableLanguages: List<SupportedLanguage> = DEFAULT_LANGUAGES,
    val isLanguagePickerVisible: Boolean = false,
    val isLoadingLanguage: Boolean = false,
    val errorMessage: String? = null,
) {
    companion object {
        val DEFAULT_LANGUAGES =
            listOf(
                SupportedLanguage(code = "en", name = "English", version = "1.0.0", isDefault = true, isCached = true),
                SupportedLanguage(code = "vi", name = "Tiếng Việt", version = "1.0.0", isDefault = false, isCached = true),
            )
    }
}
