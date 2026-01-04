/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.trends.data.mappers

import com.danhdue.trends.data.model.TrendsDto
import com.danhdue.trends.domain.model.Trends

/**
 * Maps a TrendsDto (Data Layer) object to a Trends (Domain Layer) object.
 *
 * @return The mapped Trends object.
 */
fun TrendsDto.toDomain(): Trends =
    Trends(
        id = this.uniqueId,
        data = this.payload ?: "Data not available",
    )
