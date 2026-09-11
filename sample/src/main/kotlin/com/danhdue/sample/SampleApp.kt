/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.sample

import android.app.Application
import com.danhdue.plugin.di.PluginComponentProvider

class SampleApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Pure Dagger 2 Plugin Component early
        PluginComponentProvider.get(this)
    }
}
