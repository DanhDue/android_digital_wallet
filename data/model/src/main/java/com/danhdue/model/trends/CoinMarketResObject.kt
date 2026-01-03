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
import kotlinx.parcelize.RawValue

@Keep
@JsonClass(generateAdapter = true)
@Parcelize
data class CoinMarketResObject(
    @Json(name = "category") val category: String? = null,
    @Json(name = "circulating_supply") val circulatingSupply: Int? = null,
    @Json(name = "close") val close: Double? = null,
    @Json(name = "close_time") val closeTime: Long? = null,
    @Json(name = "cmc_rank") val cmcRank: Int? = null,
    @Json(name = "date_added") val dateAdded: String? = null,
    @Json(name = "date_launched") val dateLaunched: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "fully_diluted_market_cap") val fullyDilutedMarketCap: Double? = null,
    @Json(name = "high") val high: Double? = null,
    @Json(name = "id") val id: Int? = null,
    @Json(name = "infinite_supply") val infiniteSupply: Boolean? = null,
    @Json(name = "last_updated") val lastUpdated: String? = null,
    @Json(name = "logo") val logo: String? = null,
    @Json(name = "low") val low: Double? = null,
    @Json(name = "market_cap") val marketCap: Double? = null,
    @Json(name = "market_cap_dominance") val marketCapDominance: Double? = null,
    @Json(name = "max_supply") val maxSupply: Int? = null,
    @Json(name = "minted_market_cap") val mintedMarketCap: Double? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "notice") val notice: String? = null,
    @Json(name = "num_market_pairs") val numMarketPairs: Int? = null,
    @Json(name = "number_of_trades") val numberOfTrades: Int? = null,
    @Json(name = "open") val `open`: Double? = null,
    @Json(name = "open_time") val openTime: Long? = null,
    @Json(name = "percent_change_1h") val percentChange1h: Double? = null,
    @Json(name = "percent_change_24h") val percentChange24h: Double? = null,
    @Json(name = "percent_change_30d") val percentChange30d: Double? = null,
    @Json(name = "percent_change_60d") val percentChange60d: Double? = null,
    @Json(name = "percent_change_7d") val percentChange7d: Double? = null,
    @Json(name = "percent_change_90d") val percentChange90d: Double? = null,
    @Json(name = "platform") val platform: PlatformObject? = null,
    @Json(name = "price") val price: Double? = null,
    @Json(name = "quote_asset_volume") val quoteAssetVolume: Double? = null,
    @Json(name = "self_reported_circulating_supply") val selfReportedCirculatingSupply: @RawValue Any? = null,
    @Json(name = "self_reported_market_cap") val selfReportedMarketCap: @RawValue Any? = null,
    @Json(name = "slug") val slug: String? = null,
    @Json(name = "symbol") val symbol: String? = null,
    @Json(name = "timestamp") val timestamp: String? = null,
    @Json(name = "total_supply") val totalSupply: Int? = null,
    @Json(name = "tvl") val tvl: @RawValue Any? = null,
    @Json(name = "urls") val urls: CoinMarketUrlsObject? = null,
    @Json(name = "volume") val volume: Double? = null,
    @Json(name = "volume_24h") val volume24h: Double? = null,
    @Json(name = "volume_change_24h") val volumeChange24h: Double? = null
) : Parcelable
