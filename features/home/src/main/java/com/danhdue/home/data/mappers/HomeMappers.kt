/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.home.data.mappers

import com.danhdue.home.data.model.HomeDto
import com.danhdue.home.domain.model.Home

/**
 * Maps a HomeDto (Data Layer) object to a Home (Domain Layer) object.
 *
 * @return The mapped Home object.
 */
fun HomeDto.toDomain(): Home =
    Home(
        id = this.uniqueId,
        data = this.payload ?: "Data not available",
    )
