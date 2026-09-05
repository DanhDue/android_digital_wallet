/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.data.util

internal object JsonFlattener {
    /**
     * Recursively flattens an arbitrary nested JSON map structure into a flat dot-delimited
     * map of string key-value pairs (e.g. `{"a": {"b": "c"}}` becomes `{"a.b": "c"}`).
     */
    fun flatten(
        nestedMap: Map<String, Any?>,
        prefix: String = "",
    ): Map<String, String> {
        val result = mutableMapOf<String, String>()
        for ((key, value) in nestedMap) {
            val fullKey = if (prefix.isEmpty()) key else "$prefix.$key"
            when (value) {
                is Map<*, *> -> {
                    @Suppress("UNCHECKED_CAST")
                    val subMap = value as? Map<String, Any?>
                    if (subMap != null) {
                        result.putAll(flatten(subMap, fullKey))
                    }
                }
                null -> Unit
                else -> result[fullKey] = value.toString()
            }
        }
        return result
    }
}
