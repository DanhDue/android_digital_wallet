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
data class CoinMarketUrlsObject(
    @Json(name = "announcement") val announcement: List<String?>? = null,
    @Json(name = "chat") val chat: List<String?>? = null,
    @Json(name = "explorer") val explorer: List<String?>? = null,
    @Json(name = "facebook") val facebook: List<String?>? = null,
    @Json(name = "message_board") val messageBoard: List<String?>? = null,
    @Json(name = "reddit") val reddit: List<String?>? = null,
    @Json(name = "source_code") val sourceCode: List<String?>? = null,
    @Json(name = "technical_doc") val technicalDoc: List<String?>? = null,
    @Json(name = "twitter") val twitter: List<String?>? = null,
    @Json(name = "website") val website: List<String?>? = null
) : Parcelable
