/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform.deeplink

import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/**
 * Pure JVM deep link parser.
 *
 * Normalises incoming URI strings from multiple sources into a unified [DeepLink] model.
 *
 * Normalisation rules:
 * | Input shape | `feature` | `segments` |
 * |---|---|---|
 * | `myapp://settings/profile` | authority → `"settings"` | path → `["profile"]` |
 * | `https://host/settings/profile` | first path segment → `"settings"` | remainder → `["profile"]` |
 *
 * Query parameters are percent-decoded using UTF-8. In accordance with standard parameter overriding,
 * if duplicate keys occur in the query string, **the last occurrence wins**.
 *
 * This parser uses `java.net.URI` without any Android dependencies, keeping it usable
 * across all modules and pure JVM unit tests without Robolectric.
 *
 * Returns `null` on malformed, unparseable, or non-hierarchical URIs without throwing.
 */
object DeepLinkParser {
    private const val SCHEME_DELIMITER = "://"

    /**
     * Parses the given [uriString] into a normalised [DeepLink], or returns `null`
     * if the URI is malformed, blank, non-hierarchical, or missing a feature identifier.
     */
    fun parse(uriString: String): DeepLink? {
        if (uriString.isBlank()) return null

        val uri = parseUriOrNull(uriString)
        val target = uri?.let(::extractFeatureAndSegments)

        return target?.let { (feature, segments) ->
            DeepLink(
                raw = uriString,
                feature = feature,
                segments = segments,
                params = parseQueryParams(uri.rawQuery),
            )
        }
    }

    private fun parseUriOrNull(uriString: String): URI? {
        val direct =
            try {
                URI(uriString).takeUnless { it.isOpaque }
            } catch (_: Exception) {
                null
            }
        return direct ?: parseSanitizedUriOrNull(uriString)
    }

    private fun parseSanitizedUriOrNull(uriString: String): URI? {
        // Fallback for custom schemes with underscores (e.g. "acme_wallet://...")
        // which are common in Android intent-filters but rejected by strict RFC 2396 java.net.URI.
        val delimiterIndex = uriString.indexOf(SCHEME_DELIMITER)
        if (delimiterIndex > 0) {
            val rawScheme = uriString.substring(0, delimiterIndex)
            val remainder = uriString.substring(delimiterIndex + SCHEME_DELIMITER.length)
            if (rawScheme.contains('_')) {
                val sanitizedScheme = rawScheme.replace('_', '-')
                return try {
                    URI("$sanitizedScheme://$remainder").takeUnless { it.isOpaque }
                } catch (_: Exception) {
                    null
                }
            }
        }
        return null
    }

    private fun extractFeatureAndSegments(uri: URI): Pair<String, List<String>>? {
        val scheme = uri.scheme?.lowercase() ?: return null

        val pathSegments =
            (uri.rawPath ?: "")
                .split('/')
                .filter { it.isNotEmpty() }
                .map(::decode)

        return if (scheme == "http" || scheme == "https") {
            pathSegments.firstOrNull()?.let { feature ->
                Pair(feature, pathSegments.drop(1))
            }
        } else {
            uri.authority?.takeIf { it.isNotBlank() }?.let { authority ->
                Pair(decode(authority), pathSegments)
            }
        }
    }

    private fun parseQueryParams(rawQuery: String?): Map<String, String> {
        if (rawQuery.isNullOrEmpty()) return emptyMap()

        val params = mutableMapOf<String, String>()
        for (pair in rawQuery.split('&')) {
            if (pair.isEmpty()) continue
            val eqIndex = pair.indexOf('=')
            val key: String
            val value: String
            if (eqIndex >= 0) {
                key = decode(pair.substring(0, eqIndex))
                value = decode(pair.substring(eqIndex + 1))
            } else {
                key = decode(pair)
                value = ""
            }
            params[key] = value
        }
        return params
    }

    private fun decode(value: String): String =
        try {
            URLDecoder.decode(value, StandardCharsets.UTF_8.name())
        } catch (_: Exception) {
            value
        }
}
