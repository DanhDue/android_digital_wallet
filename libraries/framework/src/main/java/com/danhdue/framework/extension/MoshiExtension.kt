/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.framework.extension

import android.content.res.AssetManager
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.IOException

val moshi: Moshi =
    Moshi
        .Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

inline fun <reified T> String.fromJson(): T? =
    try {
        val jsonAdapter = moshi.adapter(T::class.java)
        jsonAdapter.fromJson(this)
    } catch (ex: Exception) {
        null
    }

inline fun <reified T> String.fromJsonList(): List<T>? =
    try {
        val type = Types.newParameterizedType(MutableList::class.java, T::class.java)
        val jsonAdapter: JsonAdapter<List<T>> = moshi.adapter(type)
        jsonAdapter.fromJson(this)
    } catch (ex: Exception) {
        null
    }

inline fun <reified T> T.toJson(): String =
    try {
        val jsonAdapter =
            moshi
                .adapter(T::class.java)
                .serializeNulls()
                .lenient()
        jsonAdapter.toJson(this)
    } catch (ex: Exception) {
        ""
    }

inline fun <reified T> T.toJsonString(): String? =
    try {
        val jsonAdapter =
            moshi
                .adapter(T::class.java)
                .serializeNulls()
                .lenient()
                .indent("   ")
        jsonAdapter.toJson(this)
    } catch (ex: Exception) {
        null
    }

inline fun <reified T> getObjectFromJsonFile(
    assets: AssetManager,
    fileName: String,
): T? {
    var json: String? = null
    try {
        val inputStream = assets.open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        json = String(buffer, Charsets.UTF_8)
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return json?.let {
        val jsonAdapter =
            moshi
                .adapter(T::class.java)
                .serializeNulls()
                .lenient()
        jsonAdapter.fromJson(json)
    }
}

fun getJsonStringFromFile(
    assets: AssetManager,
    fileName: String,
): String {
    var json = ""
    try {
        val inputStream = assets.open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        json = String(buffer, Charsets.UTF_8)
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return json
}
