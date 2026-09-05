/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform.localization

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * CompositionLocal to access the [AppLocalizationManager] throughout Compose hierarchy.
 */
@Suppress("CompositionLocalAllowlist")
val LocalAppLocalizationManager =
    staticCompositionLocalOf<AppLocalizationManager?> {
        null
    }
