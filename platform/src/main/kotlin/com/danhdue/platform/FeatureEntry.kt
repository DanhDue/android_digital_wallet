/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform

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
}
