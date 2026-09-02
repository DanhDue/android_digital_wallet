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
import org.junit.Ignore
import org.junit.Test

/**
 * Cross-feature boundary rules — **K1** (no feature imports another feature
 * unless whitelisted) and **K6** (only the host may aggregate multiple features).
 * Epic design §6.1.
 *
 * Both are report-only in Phase 0 (`@Ignore`) — their bodies are complete and
 * correct; the enabling task only removes the annotation.
 * - K1 → Task 12 (`konsist_boundary_whitelist.txt` driven to empty, then fail mode).
 * - K6 → Task 11 (after `features/home` is dissolved into `:shell`).
 */
class BoundaryRulesTest {
    private companion object {
        /** Modules allowed to depend on more than one feature (design §5, K6). */
        val HOST_MODULES = setOf("app", "shell")
        const val WHITELIST_PATH = "scripts/konsist_boundary_whitelist.txt"
    }

    @Test
    @Ignore("Phase 0: report-only — enforced in Task 12 (whitelist emptied, then fail mode). Design §9 Phase 3.")
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
    @Ignore("Phase 0: report-only — enforced in Task 11 (after features/home becomes the :shell stub). Design §9 Phase 2.")
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
