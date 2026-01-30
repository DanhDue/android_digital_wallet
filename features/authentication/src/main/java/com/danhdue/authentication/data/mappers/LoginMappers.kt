/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.data.mappers

import com.danhdue.authentication.data.models.LoginResponseDto
import com.danhdue.authentication.data.models.UserDto
import com.danhdue.authentication.domain.entities.Login
import com.danhdue.authentication.domain.entities.User

fun LoginResponseDto.toDomain(): Login =
    Login(
        accessToken = this.access ?: "",
        refreshToken = this.refresh ?: "",
        user = this.user?.toDomain() ?: User(0, "", ""),
    )

fun UserDto.toDomain(): User = User(id = this.id ?: 0, username = this.username ?: "", email = this.email ?: "")
