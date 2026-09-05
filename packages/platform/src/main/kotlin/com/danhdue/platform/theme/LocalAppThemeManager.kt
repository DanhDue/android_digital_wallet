/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform.theme

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * CompositionLocal to access the [AppThemeManager] throughout Compose hierarchy.
 */
@Suppress("CompositionLocalAllowlist")
val LocalAppThemeManager =
    staticCompositionLocalOf<AppThemeManager?> {
        null
    }
