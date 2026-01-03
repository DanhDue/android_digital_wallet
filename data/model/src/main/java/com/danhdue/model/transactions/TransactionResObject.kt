/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.model.transactions

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class TransactionResObject(
    @Json(name = "account_inputs") val accountInputs: List<AccountInputsObject?>? = null,
    @Json(name = "signature") val signature: String? = null,
    @Json(name = "token_balances") val balances: List<TokenBalancesObject?>? = null,
    @Json(name = "overview") val overview: TransactionOverviewObject? = null,
    @Json(name = "transaction_type") val type: String? = null
) : Parcelable
