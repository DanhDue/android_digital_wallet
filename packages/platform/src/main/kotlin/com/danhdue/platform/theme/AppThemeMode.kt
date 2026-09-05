/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform.theme

/**
 * High-level theme mode preference for the application.
 */
enum class AppThemeMode {
    /** Follows the system-wide dark/light mode setting. */
    SYSTEM,

    /** Forces light theme across the application. */
    LIGHT,

    /** Forces dark theme across the application. */
    DARK,
}
