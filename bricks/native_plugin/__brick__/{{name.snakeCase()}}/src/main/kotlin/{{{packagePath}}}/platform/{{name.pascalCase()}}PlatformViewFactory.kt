/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package {{package}}.platform

import android.content.Context
import {{package}}.presentation.{{name.pascalCase()}}PlatformView
import {{package}}.presentation.{{name.pascalCase()}}ViewModel
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class {{name.pascalCase()}}PlatformViewFactory(
    private val viewModelProvider: () -> {{name.pascalCase()}}ViewModel
) : PlatformViewFactory(StandardMessageCodec.INSTANCE) {

    constructor(viewModel: {{name.pascalCase()}}ViewModel) : this({ viewModel })

    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        return {{name.pascalCase()}}PlatformView(context, viewId, args, viewModelProvider())
    }
}
