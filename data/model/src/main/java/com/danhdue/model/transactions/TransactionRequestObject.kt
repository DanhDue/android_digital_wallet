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
data class TransactionRequestObject(
    @Json(name = "before") val before: String? = null,
    @Json(name = "limit") val limit: Int? = null,
    @Json(name = "owner") val owner: String? = null,
    @Json(name = "until") val until: String? = null
) : Parcelable
