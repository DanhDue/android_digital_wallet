/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.wallet.data.mappers

import com.danhdue.wallet.data.model.MyTokensDto
import com.danhdue.wallet.domain.model.MyTokens

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
