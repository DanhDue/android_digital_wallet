/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.shell

import com.danhdue.platform.deeplink.AppDeepLinks
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Validates that every registered [com.danhdue.platform.deeplink.FeatureEntryPoint] in
 * `:platform.AppDeepLinks` targeting a shell tab corresponds to an actual [ShellTab] index.
 *
 * This contract test enforces architectural boundary consistency between `:platform` (which
 * defines integer tab indices to avoid depending on `:shell`) and `:shell` (which defines
 * the concrete tabs).
 */
class DeepLinkEntryPointContractTest {
    @Test
    fun `every registered feature entry point with a tab maps to a valid ShellTab`() {
        val tabEntries = AppDeepLinks.entryPoints.filter { it.tab != null }
        for (entry in tabEntries) {
            val tab = ShellTab.fromIndex(entry.tab!!)
            assertNotNull(
                "Entry point for feature '${entry.feature}' references tab index ${entry.tab}, " +
                    "which has no corresponding ShellTab",
                tab,
            )
        }
    }
}
