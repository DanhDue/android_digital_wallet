/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.splash.data.mappers

import com.danhdue.splash.data.model.SplashDto
import com.danhdue.splash.domain.model.Splash

/**
 * Maps a SplashDto (Data Layer) object to a Splash (Domain Layer) object.
 *
 * @return The mapped Splash object.
 */
fun SplashDto.toDomain(): Splash =
    Splash(
        id = this.uniqueId,
        data = this.payload ?: "Data not available",
    )
