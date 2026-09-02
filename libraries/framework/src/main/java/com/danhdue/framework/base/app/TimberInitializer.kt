/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.framework.base.app

import android.app.Application
import com.danhdue.core.base.app.AppInitializer
import com.danhdue.core.utils.CrashReportingTree
import com.danhdue.framework.BuildConfig.CRASHLYTIC_IS_ENABLE
import timber.log.Timber
import timber.log.Timber.Forest.plant

class TimberInitializer(
    private val isDev: Boolean,
) : AppInitializer {
    override fun init(application: Application) {
        if (isDev) {
            plant(Timber.DebugTree())
        } else {
            if (CRASHLYTIC_IS_ENABLE) {
                plant(FirebaseCrashlyticsReportTree())
            } else {
                plant(CrashReportingTree())
            }
        }
    }
}
