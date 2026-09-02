/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.data.mappers

import com.danhdue.settings.data.model.ProfileDto
import com.danhdue.settings.domain.model.Profile

/**
 * Maps a ProfileDto (Data Layer) object to a Profile (Domain Layer) object.
 *
 * @return The mapped Profile object.
 */
internal fun ProfileDto.toDomain(): Profile =
    Profile(
        id = this.uniqueId,
        data = this.payload ?: "Data not available",
    )
