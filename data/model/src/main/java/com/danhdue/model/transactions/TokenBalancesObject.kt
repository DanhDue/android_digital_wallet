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
data class TokenBalancesObject(
    @Json(name = "address") val address: String? = null,
    @Json(name = "changes") val changes: Int? = null,
    @Json(name = "post_balance") val postBalance: String? = null,
    @Json(name = "token") val token: String? = null
) : Parcelable
