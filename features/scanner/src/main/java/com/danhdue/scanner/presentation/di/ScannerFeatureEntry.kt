/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.scanner.presentation.di

import com.danhdue.platform.AppRoutes
import com.danhdue.platform.EntryProviderInstaller
import com.danhdue.platform.FeatureEntry
import com.danhdue.scanner.presentation.ScannerRoot

/**
 * Runtime navigation entry point for the on-demand `scanner` Dynamic Feature
 * Module (Task 14, design §4.4).
 *
 * An install-time feature contributes its entries through Hilt `@IntoSet`
 * multibinding at compile time; a downloaded split is invisible to the host Hilt
 * graph, so `:shell` discovers this class through `ServiceLoader`
 * (`src/main/resources/META-INF/services/com.danhdue.platform.FeatureEntry`)
 * after `SplitCompat.install(...)` and folds [installer] into the set of
 * [EntryProviderInstaller]s feeding `NavDisplay`.
 *
 * Must have a public no-arg constructor — `ServiceLoader` instantiates it
 * reflectively.
 */
class ScannerFeatureEntry : FeatureEntry {
    override fun installer(): EntryProviderInstaller =
        {
            entry<AppRoutes.ScannerRoute> {
                ScannerRoot(onEvent = {})
            }
        }
}
