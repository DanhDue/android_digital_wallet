/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.framework.base.app

import android.app.Application
import androidx.multidex.MultiDex
import com.danhdue.core.base.app.AppInitializer

class MultiDexInitializer : AppInitializer {
    override fun init(application: Application) {
        MultiDex.install(application)
    }
}
