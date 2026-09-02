/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.core.base.app

import android.app.Application

class AppInitializerImpl(
    private vararg val initializers: AppInitializer,
) : AppInitializer {
    override fun init(application: Application) {
        initializers.forEach {
            it.init(application)
        }
    }
}
