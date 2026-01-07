/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.mywallet.data.mappers

import com.danhdue.mywallet.data.model.MyTokensDto
import com.danhdue.mywallet.domain.model.MyTokens

/**
 * Maps a MyTokensDto (Data Layer) object to a MyTokens (Domain Layer) object.
 *
 * @return The mapped MyTokens object.
 */
fun MyTokensDto.toDomain(): MyTokens =
    MyTokens(
        id = this.uniqueId,
        data = this.payload ?: "Data not available",
    )
