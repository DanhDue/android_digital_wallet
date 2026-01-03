/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.model.trends

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class CoinMarketRequestObject(
    @Json(name = "convert") val convert: String? = null,
    @Json(name = "include_metadata") val includeMetadata: String? = null,
    @Json(name = "include_ohlcv") val includeOhlcv: String? = null,
    @Json(name = "limit") val limit: String? = null,
    @Json(name = "sort") val sort: String? = null,
    @Json(name = "sort_dir") val sortDir: String? = null,
    @Json(name = "start") val start: String? = null
) : Parcelable
