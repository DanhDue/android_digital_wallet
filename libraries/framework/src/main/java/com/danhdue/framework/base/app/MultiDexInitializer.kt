package com.danhdue.framework.base.app

import androidx.multidex.MultiDex

class MultiDexInitializer: AppInitializer {
    override fun init(coreApp: CoreApplication) {
        MultiDex.install(coreApp)
    }
}
