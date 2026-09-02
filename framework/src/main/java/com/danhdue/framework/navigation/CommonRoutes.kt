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
 * Defines the navigation route for the Host tab shell (`:shell` — `ShellRoot`).
 * Lives in framework to allow cross-module navigation without circular dependencies.
 * (Task 11 relocates this key, alongside [LoginRoute], from `:framework` to `:platform`.)
 */
@Serializable
data object ShellRoute : NavKey

/**
 * Defines the navigation route for the Login screen.
 * Moved to framework to allow cross-module navigation without circular dependencies.
 */
@Serializable
data object LoginRoute : NavKey
