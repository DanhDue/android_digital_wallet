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
data class TransactionOverviewObject(
    @Json(name = "compute_units_consumed") val computeUnitsConsumed: Int? = null,
    @Json(name = "confirmation_status") val confirmationStatus: String? = null,
    @Json(name = "confirmations") val confirmations: String? = null,
    @Json(name = "fee") val fee: Double? = null,
    @Json(name = "payer_address") val payerAddress: String? = null,
    @Json(name = "recent_blockhash") val recentBlockhash: String? = null,
    @Json(name = "reserved_cus") val reservedCus: Int? = null,
    @Json(name = "result") val result: String? = null,
    @Json(name = "signature") val signature: List<String?>? = null,
    @Json(name = "slot") val slot: Int? = null,
    @Json(name = "timestamp") val timestamp: Int? = null,
    @Json(name = "transaction_cost") val transactionCost: Double? = null,
    @Json(name = "transaction_version") val transactionVersion: String? = null
) : Parcelable
