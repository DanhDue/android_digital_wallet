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
data class AccountInputsObject(
    @Json(name = "address") val address: String? = null,
    @Json(name = "changes") val changes: Double? = null,
    @Json(name = "details") val details: List<String?>? = null,
    @Json(name = "is_payer") val isPayer: Boolean? = null,
    @Json(name = "post_balance") val postBalance: Double? = null
) : Parcelable
