/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.konsist

import com.danhdue.konsist.support.ArchScope
import com.danhdue.konsist.support.BoundaryWhitelist
import com.danhdue.konsist.support.Feature
import com.danhdue.konsist.support.FeatureEdge
import com.danhdue.konsist.support.RepoRoot
import com.danhdue.konsist.support.assertNoViolations
import com.danhdue.konsist.support.packageName
import org.junit.Test

/**
 * Cross-feature boundary rules — **K1** (no feature imports another feature
 * unless whitelisted) and **K6** (only the host may aggregate multiple features).
 * Epic design §6.1.
 *
 * - K6 → ENFORCED in Task 11. Passes clean: Task 10 dissolved `features/home` and Task 11
 *   dropped `:shell`'s 5 `:features:*` deps (every tab seed `*Route` moved to
 *   `:platform.AppRoutes`), so no non-host module imports more than one feature.
 * - K1 → ENFORCED since Task 12. The whitelist is empty; any cross-feature import fails the build.
 *   Violations are resolved via `AppRoutes` navigation, `AppEvent` signals, or dependency inversion
 *   with `:core` interfaces. The gate is hard-enforced and no exceptions are permitted.
 */
class BoundaryRulesTest {
    private companion object {
        /** Modules allowed to depend on more than one feature (design §5, K6). */
        val HOST_MODULES = setOf("app", "shell")
        const val WHITELIST_PATH = "scripts/konsist_boundary_whitelist.txt"
    }

    @Test
    fun `K1 - a feature must not import another feature unless whitelisted`() {
        val whitelist: Set<FeatureEdge> = BoundaryWhitelist.parseFile(RepoRoot.resolve(WHITELIST_PATH))

        val offenders =
            ArchScope
                .featureFiles()
                .flatMap { file ->
                    val owner = Feature.owning(file.packageName) ?: return@flatMap emptyList<String>()
                    file.imports.mapNotNull { import ->
                        val target = Feature.owning(import.name) ?: return@mapNotNull null
                        if (target == owner) return@mapNotNull null
                        val edge = FeatureEdge(owner.moduleName, target.moduleName)
                        if (edge in whitelist) {
                            null
                        } else {
                            "${file.path}: ${owner.moduleName} ${BoundaryWhitelist.ARROW} ${target.moduleName} " +
                                "(import ${import.name})"
                        }
                    }
                }

        assertNoViolations(
            ruleId = "K1",
            rule =
                "a file in one feature must not import another feature's package unless the edge " +
                    "`from${BoundaryWhitelist.ARROW}to` is listed in $WHITELIST_PATH.",
            fix =
                "navigate via `AppRoutes` / signal via `AppEvent` / invert with a `:core` interface; " +
                    "or add a temporary `from${BoundaryWhitelist.ARROW}to` whitelist line with a migration owner.",
            offenders = offenders,
        )
    }

    @Test
    fun `K6 - only host modules may aggregate more than one feature`() {
        val offenders =
            ArchScope
                .mainFiles
                .groupBy { ArchScope.moduleOf(it.path) }
                .filterKeys { it.isNotEmpty() && it !in HOST_MODULES }
                .mapNotNull { (module, files) ->
                    val ownFeature = files.firstNotNullOfOrNull { Feature.owning(it.packageName) }
                    val importedFeatures =
                        files
                            .flatMap { file -> file.imports.mapNotNull { Feature.owning(it.name) } }
                            .toSet()
                            .minus(setOfNotNull(ownFeature))
                    if (importedFeatures.size > 1) {
                        "$module imports ${importedFeatures.map { it.moduleName }.sorted()}"
                    } else {
                        null
                    }
                }

        assertNoViolations(
            ruleId = "K6",
            rule =
                "only `:app` / `:shell` may depend on more than one `com.danhdue.features.*` package; " +
                    "a feature that aggregates siblings is taking on a host responsibility.",
            fix = "move the aggregation into `:shell`, or replace the sibling imports with `AppRoutes` navigation.",
            offenders = offenders,
        )
    }
}
