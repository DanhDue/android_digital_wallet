/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.wallet

import com.danhdue.framework.base.app.AppInitializer
import com.danhdue.framework.base.app.CoreApplication
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DigitalWalletApp : CoreApplication() {
    @Inject
    lateinit var initializer: AppInitializer

    override fun onCreate() {
        super.onCreate()
        // Initialize all app initializers including Flipper
        initializer.init(this)
    }
}
