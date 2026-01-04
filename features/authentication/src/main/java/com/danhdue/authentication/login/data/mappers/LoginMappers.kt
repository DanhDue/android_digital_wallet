/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.login.data.mappers

import com.danhdue.authentication.login.data.model.LoginDto
import com.danhdue.authentication.login.domain.model.Login

/**
 * Maps a LoginDto (Data Layer) object to a Login (Domain Layer) object.
 *
 * @return The mapped Login object.
 */
fun LoginDto.toDomain(): Login =
    Login(
        id = this.uniqueId,
        data = this.payload ?: "Data not available",
    )
