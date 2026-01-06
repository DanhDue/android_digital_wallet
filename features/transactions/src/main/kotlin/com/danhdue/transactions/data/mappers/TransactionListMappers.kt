/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.transactions.data.mappers

import com.danhdue.transactions.data.model.TransactionListDto
import com.danhdue.transactions.domain.model.TransactionList

/**
 * Maps a TransactionListDto (Data Layer) object to a TransactionList (Domain Layer) object.
 *
 * @return The mapped TransactionList object.
 */
fun TransactionListDto.toDomain(): TransactionList =
    TransactionList(
        id = this.uniqueId,
        data = this.payload ?: "Data not available",
    )
