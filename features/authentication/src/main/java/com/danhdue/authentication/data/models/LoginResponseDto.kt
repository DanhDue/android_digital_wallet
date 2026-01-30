package com.danhdue.authentication.data.models

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class LoginResponseDto(
        @Json(name = "access") val access: String? = null,
        @Json(name = "refresh") val refresh: String? = null,
        @Json(name = "user") val user: UserDto? = null
)
