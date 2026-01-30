/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.data.mappers

import com.danhdue.authentication.data.models.RegisterDto
import com.danhdue.authentication.domain.entities.Register

/**
 * Maps a RegisterDto (Data Layer) object to a Register (Domain Layer) object.
 *
 * @return The mapped Register object.
 */
fun RegisterDto.toDomain(): Register =
    Register(
        id = this.uniqueId,
        data = this.payload ?: "Data not available",
    )
