package com.danhdue.authentication.data.models

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class UserDto(
        @Json(name = "email") val email: String? = null,
        @Json(name = "id") val id: Int? = null,
        @Json(name = "username") val username: String? = null
)
