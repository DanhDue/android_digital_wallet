/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.framework.base.app

import com.danhdue.framework.BuildConfig.CRASHLYTIC_IS_ENABLE
import com.danhdue.framework.utils.CrashReportingTree
import timber.log.Timber
import timber.log.Timber.Forest.plant

class TimberInitializer(
    private val isDev: Boolean,
) : AppInitializer {
    override fun init(coreApp: CoreApplication) {
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
