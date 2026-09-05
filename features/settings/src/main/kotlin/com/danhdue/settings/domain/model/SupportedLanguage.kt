/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.domain.model

/**
 * Represents a supported language in the application.
 *
 * @property code ISO language/locale code (e.g. "en", "vi", "ja_JP")
 * @property name Human-readable language name (e.g. "English", "Tiếng Việt")
 * @property version Currently deployed translation schema version
 * @property isDefault Whether this language is the backend default
 * @property isActive Whether this language is active and selectable
 * @property isCached Whether translations for this language are already stored locally
 */
data class SupportedLanguage(
    val code: String,
    val name: String,
    val version: String,
    val isDefault: Boolean = false,
    val isActive: Boolean = true,
    val isCached: Boolean = false,
)
