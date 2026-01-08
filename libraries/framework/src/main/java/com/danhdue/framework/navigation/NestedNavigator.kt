/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.framework.navigation

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Interface for navigating within a nested tab.
 */
interface NestedNavigator {
    fun navigate(destination: Any)

    fun popBackStack()
}

/**
 * CompositionLocal to provide a nested navigator for tabs.
 */
val LocalNestedNavigator =
    staticCompositionLocalOf<NestedNavigator> {
        object : NestedNavigator {
            override fun navigate(destination: Any) {
                // No-op
            }

            override fun popBackStack() {
                // No-op
            }
        }
    }
