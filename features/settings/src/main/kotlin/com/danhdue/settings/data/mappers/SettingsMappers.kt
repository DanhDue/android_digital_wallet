/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.data.mappers

import com.danhdue.settings.data.model.SettingsDto
import com.danhdue.settings.domain.model.Settings

/**
 * Maps a SettingsDto (Data Layer) object to a Settings (Domain Layer) object.
 *
 * @return The mapped Settings object.
 */
internal fun SettingsDto.toDomain(): Settings =
    Settings(
        id = this.uniqueId,
        data = this.payload ?: "Data not available",
    )
