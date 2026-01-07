/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.mywallet.data.mappers

import com.danhdue.mywallet.data.model.MyNFTsDto
import com.danhdue.mywallet.domain.model.MyNFTs

/**
 * Maps a MyNFTsDto (Data Layer) object to a MyNFTs (Domain Layer) object.
 *
 * @return The mapped MyNFTs object.
 */
fun MyNFTsDto.toDomain(): MyNFTs =
    MyNFTs(
        id = this.uniqueId,
        data = this.payload ?: "Data not available",
    )
