/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.data.mappers

import com.danhdue.authentication.data.model.AuthenticatorDto
import com.danhdue.authentication.domain.model.Authenticator

/**
 * Maps a AuthenticatorDto (Data Layer) object to a Authenticator (Domain Layer) object.
 *
 * @return The mapped Authenticator object.
 */
fun AuthenticatorDto.toDomain(): Authenticator {
    return Authenticator(
        id = this.uniqueId,
        data = this.payload ?: "Data not available"
    )
}