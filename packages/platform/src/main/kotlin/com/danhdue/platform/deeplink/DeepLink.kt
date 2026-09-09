/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform.deeplink

/**
 * Normalised representation of an incoming deep link.
 *
 * Links from all sources (custom scheme `myapp://`, Android App Links `https://`,
 * push notification payloads, and internal dispatch) are parsed into this single
 * shape before being evaluated by Tier 1 [AppDeepLinks], resolvers, or guards.
 *
 * @property raw The raw, unparsed URI string as received. Preserved for logging and for
 *               storing in [PendingDeepLinkStore] to replay verbatim after login.
 * @property feature The target feature identifier (Tier-1 routing key, e.g., "settings", "scanner").
 *                   Extracted from authority for custom scheme, or from the first path segment for App Links.
 * @property segments Path segments below the feature (Tier-2 routing input, e.g., ["profile"]).
 *                    Empty if the link targets only the feature root.
 * @property params Decoded query parameters. When duplicate keys appear in the query string,
 *                  the last occurrence wins.
 */
data class DeepLink(
    val raw: String,
    val feature: String,
    val segments: List<String>,
    val params: Map<String, String>,
)
