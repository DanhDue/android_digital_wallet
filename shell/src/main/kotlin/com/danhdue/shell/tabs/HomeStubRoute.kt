/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.shell.tabs

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * `:shell`-local navigation key for the Home tab's stub page.
 *
 * Provenance: `features/home` was dissolved when its tab-shell UI moved into
 * `:shell` (Task 10) and the digital-wallet domain was stripped to a reusable
 * template (Task 13, design §8). What was left of the Home feature — no real
 * content — becomes the [HomeStubPage] placeholder. The key stays inside `:shell`
 * and is **not** promoted to `:platform.AppRoutes`: nothing outside `:shell`
 * navigates to it, so it never crosses a feature boundary (Konsist K9).
 */
@Serializable
data object HomeStubRoute : NavKey
