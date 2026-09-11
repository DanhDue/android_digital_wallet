/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.plugin.platform

import android.content.Context
import com.danhdue.plugin.presentation.MyPlatformView
import com.danhdue.plugin.presentation.MyPluginViewModel
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class MyPlatformViewFactory(
    private val viewModelProvider: () -> MyPluginViewModel
) : PlatformViewFactory(StandardMessageCodec.INSTANCE) {

    constructor(viewModel: MyPluginViewModel) : this({ viewModel })

    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        return MyPlatformView(context, viewId, args, viewModelProvider())
    }
}
