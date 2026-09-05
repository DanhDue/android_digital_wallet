/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.data.local

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SupportedLanguageItem(
    @Json(name = "code")
    val code: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "version")
    val version: String,
    @Json(name = "is_default")
    val isDefault: Boolean = false,
    @Json(name = "is_active")
    val isActive: Boolean = true,
    @Json(name = "is_cached")
    val isCached: Boolean = false,
)
