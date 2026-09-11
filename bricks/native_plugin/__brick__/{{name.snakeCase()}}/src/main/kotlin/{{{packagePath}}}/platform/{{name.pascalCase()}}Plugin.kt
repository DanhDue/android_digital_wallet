/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package {{package}}.platform

import {{package}}.di.{{name.pascalCase()}}ComponentProvider
import io.flutter.embedding.engine.plugins.FlutterPlugin

class {{name.pascalCase()}}Plugin : FlutterPlugin {

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        val component = {{name.pascalCase()}}ComponentProvider.get(binding.applicationContext)

        // Setup Pigeon Host Api for Headless IPC
        {{name.pascalCase()}}HostApi.setUp(
            binding.binaryMessenger,
            {{name.pascalCase()}}HostApiImpl(component.getDataUseCase())
        )

        // Register Jetpack Compose PlatformView
        binding.platformViewRegistry.registerViewFactory(
            VIEW_TYPE,
            {{name.pascalCase()}}PlatformViewFactory {
                component.get{{name.pascalCase()}}ViewModel()
            }
        )
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        {{name.pascalCase()}}HostApi.setUp(binding.binaryMessenger, null)
    }

    companion object {
        const val VIEW_TYPE = "{{package}}/native_view"
    }
}
