/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package {{package}}.presentation

import android.content.Context
import android.view.View
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import io.flutter.plugin.platform.PlatformView

class {{name.pascalCase()}}PlatformView(
    context: Context,
    viewId: Int,
    args: Any?,
    private val viewModel: {{name.pascalCase()}}ViewModel
) : PlatformView {

    private val composeView: ComposeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
        setContent {
            {{name.pascalCase()}}Screen(viewModel = viewModel)
        }
    }

    override fun getView(): View = composeView

    override fun dispose() {
        composeView.disposeComposition()
    }
}
