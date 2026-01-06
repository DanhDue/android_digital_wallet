/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.transactions.data.mappers

import com.danhdue.transactions.data.model.TransactionDetailDto
import com.danhdue.transactions.domain.model.TransactionDetail

/**
 * Maps a TransactionDetailDto (Data Layer) object to a TransactionDetail (Domain Layer) object.
 *
 * @return The mapped TransactionDetail object.
 */
fun TransactionDetailDto.toDomain(): TransactionDetail =
    TransactionDetail(
        id = this.uniqueId,
        data = this.payload ?: "Data not available",
    )
