/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.konsist

import com.danhdue.konsist.support.ArchScope
import com.danhdue.konsist.support.DfmModules
import com.danhdue.konsist.support.Feature
import com.danhdue.konsist.support.assertNoViolations
import com.danhdue.konsist.support.packageName
import org.junit.Ignore
import org.junit.Test

/**
 * Host / layering rules — **K7** (`:core` is the floor), **K8** (features never
 * import host internals), **K9** (`NavKey`s used cross-feature live in `:platform`).
 * Epic design §6.1.
 *
 * K7 and K8 are ENFORCED; K9 stays a report-only body (`@Ignore`) whose assertion is
 * complete — Task 11 removes its annotation (cross-feature `NavKey` relocation, design §9
 * Phase 2). K7 was enabled in Task 9 once `:core` was extracted (design §9 Phase 1).
 */
class HostRulesTest {
    private companion object {
        /** `:app` namespace — host-internal package, off-limits to features (design §6.1 K8). */
        const val HOST_PACKAGE = "com.danhdue.androiddigitalwallet"

        /** `:core` package — extracted in Task 5; the dependency floor (design §6.1 K7). */
        const val CORE_PACKAGE = "com.danhdue.core"

        /** Upper-layer packages `:core` must never import (design §4.2). */
        val ABOVE_CORE =
            listOf(
                "com.danhdue.framework",
                "com.danhdue.network",
                "com.danhdue.uikit",
                "com.danhdue.platform",
                "com.danhdue.shell",
            )
    }

    @Test
    fun `K8 - features must not import host-internal com_danhdue_androiddigitalwallet`() {
        val dfmExemptPrefixes = DfmModules.exemptPackagePrefixes()

        val offenders =
            ArchScope
                .featureFiles()
                .filterNot { file -> dfmExemptPrefixes.any { file.packageName.startsWith(it) } }
                .flatMap { file -> file.imports.map { file to it.name } }
                .filter { (_, import) -> import == HOST_PACKAGE || import.startsWith("$HOST_PACKAGE.") }
                .map { (file, import) -> "${file.path} imports $import" }

        assertNoViolations(
            ruleId = "K8",
            rule =
                "a feature module must not import `$HOST_PACKAGE.*` (host/composition-root internals). " +
                    "Only a module listed in `:app`'s `android.dynamicFeatures` is exempt " +
                    "(a DFM is forced to depend on `:app`).",
            fix =
                "move the shared type into `:platform` (AppRoutes / AppEvent / FeatureEntry) and import it from there.",
            offenders = offenders,
        )
    }

    @Test
    fun `K7 - core must not depend on any upper layer`() {
        val offenders =
            ArchScope
                .mainFiles
                .filter { it.packageName == CORE_PACKAGE || it.packageName.startsWith("$CORE_PACKAGE.") }
                .flatMap { file -> file.imports.map { file to it.name } }
                .filter { (_, import) ->
                    ABOVE_CORE.any { import == it || import.startsWith("$it.") } ||
                        Feature.owning(import) != null
                }.map { (file, import) -> "${file.path} imports $import" }

        assertNoViolations(
            ruleId = "K7",
            rule =
                "`:core` is the dependency floor: it must not import `:framework`, `:network`, `:ui_kit`, " +
                    "`:platform`, `:shell` or any `:features:*` package.",
            fix = "invert the dependency — put the shared abstraction in `:core` and let the upper layer implement it.",
            offenders = offenders,
        )
    }

    @Test
    @Ignore("Phase 0: report-only — enforced in Task 11 (cross-feature NavKey relocation, design §9 Phase 2).")
    fun `K9 - a NavKey used outside its own feature must be declared in platform`() {
        // Every *Route : NavKey declared inside a feature.
        val featureRoutes =
            ArchScope
                .objects()
                .filter { it.name.endsWith("Route") }
                .filter { declaration -> declaration.parents().any { it.name == "NavKey" } }
                .mapNotNull { route ->
                    val owner = Feature.owning(route.packageName) ?: return@mapNotNull null
                    route.name to owner
                }.toMap()

        // Files that reference a route by simple name while sitting outside its owning feature.
        val offenders =
            ArchScope
                .mainFiles
                .flatMap { file -> file.imports.map { file to it.name } }
                .mapNotNull { (file, import) ->
                    val simpleName = import.substringAfterLast('.')
                    val owner = featureRoutes[simpleName] ?: return@mapNotNull null
                    val importerFeature = Feature.owning(file.packageName)
                    if (importerFeature == owner) return@mapNotNull null
                    "${file.path} uses ${owner.moduleName}'s `$simpleName` — must move to :platform AppRoutes"
                }

        assertNoViolations(
            ruleId = "K9",
            rule =
                "a `*Route : NavKey` referenced by any module other than its owning feature must be declared " +
                    "in `:platform` (`AppRoutes`), not in `..features..presentation..`.",
            fix = "relocate the route object into `com.danhdue.platform.AppRoutes` and update imports.",
            offenders = offenders,
        )
    }
}
