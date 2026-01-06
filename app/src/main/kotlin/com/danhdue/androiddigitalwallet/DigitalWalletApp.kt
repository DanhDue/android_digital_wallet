/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.androiddigitalwallet

import com.danhdue.framework.base.app.AppInitializer
import com.danhdue.framework.base.app.CoreApplication
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DigitalWalletApp : CoreApplication() {
    @Inject
    lateinit var initializer: AppInitializer
}
