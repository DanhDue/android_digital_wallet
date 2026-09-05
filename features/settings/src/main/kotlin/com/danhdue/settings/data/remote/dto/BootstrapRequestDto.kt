/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class BootstrapRequestDto(
    @Json(name = "cached_translations")
    val cachedTranslations: List<CachedTranslationDto> = emptyList(),
)

@JsonClass(generateAdapter = true)
internal data class CachedTranslationDto(
    @Json(name = "resource_id")
    val resourceId: String,
    @Json(name = "version")
    val version: String,
)
