/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.transactions.data.mappers

import com.danhdue.transactions.data.model.TransactionsDto
import com.danhdue.transactions.domain.model.Transactions

/**
 * Maps a TransactionsDto (Data Layer) object to a Transactions (Domain Layer) object.
 *
 * @return The mapped Transactions object.
 */
fun TransactionsDto.toDomain(): Transactions =
    Transactions(
        id = this.uniqueId,
        data = this.payload ?: "Data not available",
    )
