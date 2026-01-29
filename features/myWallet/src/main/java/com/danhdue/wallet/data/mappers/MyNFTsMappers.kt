/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.wallet.data.mappers

import com.danhdue.wallet.data.model.MyNFTsDto
import com.danhdue.wallet.domain.model.MyNFTs

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
