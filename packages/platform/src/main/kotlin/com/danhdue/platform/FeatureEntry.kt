/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform

import com.danhdue.platform.deeplink.DeepLinkResolver

/**
 * Contract implemented only by on-demand dynamic feature modules (Task 14).
 *
 * Install-time features contribute their navigation entries through Hilt
 * `@IntoSet` multibinding and never implement this interface. A dynamic feature
 * module is invisible to the host's Hilt graph at compile time, so once its
 * split is installed the host discovers its [EntryProviderInstaller] through
 * this interface (loaded via `ServiceLoader`).
 */
interface FeatureEntry {
    /** Returns the installer that registers this feature's navigation entries. */
    fun installer(): EntryProviderInstaller

    /**
     * Returns the [DeepLinkResolver] for this on-demand dynamic feature split, or null
     * if the feature does not contribute deep link routes.
     *
     * Defaulted to null so existing [FeatureEntry] implementations remain source-compatible.
     */
    fun resolver(): DeepLinkResolver? = null
}

/**
 * Safely extracts successfully loaded [FeatureEntry] instances from [loader].
 *
 * Drops uninstalled dynamic feature splits that throw [java.util.ServiceConfigurationError]
 * or [LinkageError] during iteration.
 */
fun featureEntriesFrom(loader: Iterable<FeatureEntry>): List<FeatureEntry> {
    val entries = mutableListOf<FeatureEntry>()
    val iterator = loader.iterator()
    while (hasNextOrStop(iterator)) {
        nextOrNull(iterator)?.let { entries += it }
    }
    return entries
}

private fun hasNextOrStop(iterator: Iterator<FeatureEntry>): Boolean =
    try {
        iterator.hasNext()
    } catch (_: java.util.ServiceConfigurationError) {
        false
    } catch (_: LinkageError) {
        false
    }

private fun nextOrNull(iterator: Iterator<FeatureEntry>): FeatureEntry? =
    try {
        iterator.next()
    } catch (_: java.util.ServiceConfigurationError) {
        null
    } catch (_: LinkageError) {
        null
    }
