/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.androiddigitalwallet

import android.content.Context
import com.danhdue.core.base.app.AppInitializer
import com.danhdue.framework.base.app.CoreApplication
import com.google.android.play.core.splitcompat.SplitCompat
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DigitalWalletApp : CoreApplication() {
    @Inject
    lateinit var initializer: AppInitializer

    /**
     * Enables Play Feature Delivery for on-demand Dynamic Feature Module splits
     * (Task 14, design §4.4). `SplitCompat.install` makes a just-installed split's
     * code + resources visible to the already-running base `Application`; it is
     * called again per-Activity by `FeatureInstallerImpl` right after a split
     * finishes installing.
     */
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        SplitCompat.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        // Initialize all app initializers including Flipper
        initializer.init(this)
    }
}
