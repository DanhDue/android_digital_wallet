/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform

import javax.inject.Inject

/**
 * Ensures the code for a feature module is present before it is navigated to.
 *
 * The real implementation lives in `:app` (Task 14) and wraps
 * `SplitInstallManager`. [NoOpFeatureInstaller] is the JVM/test default and
 * treats every module as already installed.
 */
interface FeatureInstaller {
    /**
     * Guarantees [module] is installed, then invokes [onReady].
     *
     * @param module the dynamic feature module name, e.g. `"scanner"`
     * @param onReady run once the module is available; may run synchronously
     */
    suspend fun ensureInstalled(
        module: String,
        onReady: () -> Unit,
    )
}

/**
 * [FeatureInstaller] for environments where every module is bundled at install
 * time (unit tests, plain JVM). Invokes [onReady] immediately for any [module].
 */
class NoOpFeatureInstaller @Inject constructor() : FeatureInstaller {
    override suspend fun ensureInstalled(
        module: String,
        onReady: () -> Unit,
    ) {
        onReady()
    }
}
