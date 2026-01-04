/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.splash.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) for the Splash feature received from an external source (e.g., API).
 * Its structure mirrors the data source's response.
 *
 * @property uniqueId The identifier from the API (could have a different name).
 * @property payload The raw data from the API, which could be null.
 */
@Serializable
data class SplashDto(
    @SerialName("remote_id")
    val uniqueId: String,
    @SerialName("raw_data")
    val payload: String?,
)
