/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.mywallet.data.mappers

import com.danhdue.mywallet.data.model.MyWalletDto
import com.danhdue.mywallet.domain.model.MyWallet

/**
 * Maps a MyWalletDto (Data Layer) object to a MyWallet (Domain Layer) object.
 *
 * @return The mapped MyWallet object.
 */
fun MyWalletDto.toDomain(): MyWallet =
    MyWallet(
        id = this.uniqueId,
        data = this.payload ?: "Data not available",
    )
