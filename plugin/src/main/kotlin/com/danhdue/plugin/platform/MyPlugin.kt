/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.plugin.platform

import com.danhdue.plugin.di.PluginComponentProvider
import io.flutter.embedding.engine.plugins.FlutterPlugin

class MyPlugin : FlutterPlugin {

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        val component = PluginComponentProvider.get(binding.applicationContext)

        // Setup Pigeon Host Api for Headless IPC
        MyPluginHostApi.setUp(
            binding.binaryMessenger,
            MyPluginHostApiImpl(component.getDataUseCase())
        )

        // Register Jetpack Compose PlatformView
        binding.platformViewRegistry.registerViewFactory(
            VIEW_TYPE,
            MyPlatformViewFactory {
                component.getMyPluginViewModel()
            }
        )
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        MyPluginHostApi.setUp(binding.binaryMessenger, null)
    }

    companion object {
        const val VIEW_TYPE = "com.danhdue.plugin/native_view"
    }
}
