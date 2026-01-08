/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.androiddigitalwallet

import androidx.multidex.MultiDexApplication
import com.danhdue.framework.base.app.AppInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DigitalWalletApp : MultiDexApplication() {
    @Inject
    lateinit var initializer: AppInitializer
}
