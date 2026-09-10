/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.scanner.presentation.di

import com.danhdue.platform.AppRoutes
import com.danhdue.platform.EntryProviderInstaller
import com.danhdue.platform.FeatureEntry
import com.danhdue.platform.deeplink.DeepLinkResolver
import com.danhdue.scanner.presentation.ScannerRoot

/**
 * Runtime navigation entry point for the on-demand `scanner` Dynamic Feature
 * Module (Task 14, design §4.4).
 *
 * An install-time feature contributes its entries through Hilt `@IntoSet`
 * multibinding at compile time; a downloaded split is invisible to the host Hilt
 * graph, so `:shell` discovers this class through `ServiceLoader` after
 * `SplitCompat.install(...)` and folds [installer] into the set of
 * [EntryProviderInstaller]s feeding `NavDisplay`.
 *
 * The `META-INF/services/com.danhdue.platform.FeatureEntry` file that names this
 * class lives in **`:app`** (`app/src/main/resources/...`), not this module —
 * bundletool forbids two feature splits shipping the same root resource with
 * differing content, so every on-demand `FeatureEntry` FQCN is aggregated into
 * that one base-module file (design §4.4). `mvi_feature --delivery on-demand`
 * appends to it; `remove_feature` removes the line.
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

    override fun resolver(): DeepLinkResolver = ScannerDeepLinkResolver()
}
