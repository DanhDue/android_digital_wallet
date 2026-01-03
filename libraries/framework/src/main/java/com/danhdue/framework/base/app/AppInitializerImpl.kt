package com.danhdue.framework.base.app

class AppInitializerImpl(private vararg val initializers: AppInitializer) : AppInitializer {
    override fun init(coreApp: CoreApplication) {
        initializers.forEach {
            it.init(coreApp)
        }
    }
}