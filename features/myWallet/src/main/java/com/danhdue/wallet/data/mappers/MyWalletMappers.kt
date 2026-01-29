/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.wallet.data.mappers

import com.danhdue.wallet.data.model.MyWalletDto
import com.danhdue.wallet.domain.model.MyWallet

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
