/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.framework.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object CommonRoutes

/**
 * Defines the navigation route for the Dashboard (Main) screen.
 * Moved to framework to allow cross-module navigation without circular dependencies.
 */
@Serializable
data object HomeRoute : NavKey

/**
 * Defines the navigation route for the Login screen.
 * Moved to framework to allow cross-module navigation without circular dependencies.
 */
@Serializable
data object LoginRoute : NavKey
