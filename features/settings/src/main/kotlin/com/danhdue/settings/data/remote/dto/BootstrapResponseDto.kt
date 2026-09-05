/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class BootstrapResponseDto(
    @Json(name = "available_languages")
    val availableLanguages: List<SupportedLanguageDto> = emptyList(),
    @Json(name = "stale_translations")
    val staleTranslations: List<StaleTranslationDto> = emptyList(),
)

@JsonClass(generateAdapter = true)
internal data class SupportedLanguageDto(
    @Json(name = "language_code")
    val languageCode: String,
    @Json(name = "language_name")
    val languageName: String,
    @Json(name = "version")
    val version: String,
    @Json(name = "is_default")
    val isDefault: Boolean = false,
    @Json(name = "is_active")
    val isActive: Boolean = true,
)

@JsonClass(generateAdapter = true)
internal data class StaleTranslationDto(
    @Json(name = "resource_id")
    val resourceId: String,
    @Json(name = "server_version")
    val serverVersion: String,
)
