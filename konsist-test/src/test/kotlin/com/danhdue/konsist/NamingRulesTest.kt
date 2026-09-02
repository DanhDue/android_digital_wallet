/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.konsist

import com.danhdue.konsist.support.ArchScope
import com.danhdue.konsist.support.Baseline
import com.danhdue.konsist.support.Feature
import com.danhdue.konsist.support.assertNoViolations
import com.danhdue.konsist.support.packageName
import com.danhdue.konsist.support.qualified
import org.junit.Test

/**
 * **Konsist rule K5 — naming & structural conventions** (epic design §6.1, §4.3).
 *
 * ENFORCED in Phase 0: every check fails the build on a real violation. Genuine
 * pre-existing violations are listed per-declaration in
 * `konsist-test/konsist_baseline.txt` (each with a `// TODO(task_N)` cleanup
 * pointer) and skipped here until that task removes them.
 *
 * Scope note: the design's `*RepositoryImpl` suffix is not yet the repo-wide
 * convention (implementations are currently named `Default*Repository`), so the
 * repository check enforces the load-bearing part — **domain/data placement** —
 * and leaves the suffix rename to the architecture cleanup tasks.
 */
class NamingRulesTest {
    private companion object {
        const val RULE = "K5"
        const val MVI_BASE = "MviViewModel"
        val VIEW_MODEL_BASES = setOf("MviViewModel", "MvvmViewModel")

        /** Simple name of a parent, generics stripped (`"MviViewModel<A, B>"` -> `"MviViewModel"`). */
        fun rawParentName(fullName: String): String = fullName.substringBefore('<').substringAfterLast('.').trim()
    }

    @Test
    fun `K5 - feature ViewModels extend MviViewModel`() {
        val baseline = Baseline.suppressedFor(RULE)
        val offenders =
            ArchScope
                .classes()
                .filter { it.name.endsWith("ViewModel") }
                .filter { Feature.owning(it.packageName) != null }
                .filterNot { it.name in VIEW_MODEL_BASES }
                .filterNot { koClass ->
                    koClass.parents(indirectParents = true).any { rawParentName(it.name) == MVI_BASE }
                }.map { it.qualified() }
                .filterNot { it in baseline }

        assertNoViolations(
            ruleId = RULE,
            rule =
                "every `*ViewModel` in a feature module must extend " +
                    "`com.danhdue.framework.base.mvi.MviViewModel<State, Action, Event>` " +
                    "(single `onAction` entry point, unidirectional data flow).",
            fix =
                "change the supertype to `MviViewModel<…>` and route UI intents through `onAction`; " +
                    "a deliberate, tracked exception goes in konsist_baseline.txt with a `// TODO(task_N)`.",
            offenders = offenders,
        )
    }

    @Test
    fun `K5 - use cases are named with the UseCase suffix and live in domain usecase`() {
        val featureFiles = ArchScope.featureFiles()

        val misplacedSuffix =
            featureFiles
                .flatMap { it.classes(includeNested = true) }
                .filter { it.name.endsWith("UseCase") }
                .filterNot { it.packageName.contains(".domain.usecase") }
                .map { "${it.qualified()} — *UseCase not under ..domain.usecase.." }

        val missingSuffix =
            featureFiles
                .filter { it.packageName.contains(".domain.usecase") }
                .flatMap { it.classes(includeNested = true) }
                .filterNot { it.name.endsWith("UseCase") }
                .map { "${it.qualified()} — class in ..domain.usecase.. not named *UseCase" }

        assertNoViolations(
            ruleId = RULE,
            rule = "a feature's use cases must be classes named `*UseCase`, located in `..domain.usecase..`.",
            fix = "rename the class to end with `UseCase` and/or move it under `domain/usecase`.",
            offenders = misplacedSuffix + missingSuffix,
        )
    }

    @Test
    fun `K5 - Screen composables carry the Composable annotation`() {
        val offenders =
            ArchScope
                .mainFiles
                .filter { Feature.owning(it.packageName) != null }
                .flatMap { file -> file.functions(includeNested = true).map { file to it } }
                .filter { (_, function) -> function.name.endsWith("Screen") }
                .filterNot { (_, function) -> function.hasAnnotationWithName("Composable") }
                .map { (file, function) -> "${file.path}::${function.name}()" }

        assertNoViolations(
            ruleId = RULE,
            rule = "every `*Screen` function is a Compose UI entry point and must be annotated `@Composable`.",
            fix = "add `@Composable`, or rename the function if it is not a screen.",
            offenders = offenders,
        )
    }

    @Test
    fun `K5 - Route declarations implement NavKey`() {
        val offenders =
            listOf(ArchScope.objects(), ArchScope.classes())
                .flatten()
                .filter { it.name.endsWith("Route") }
                .filterNot { declaration -> declaration.parents().any { it.name == "NavKey" } }
                .map { it.qualified() }

        assertNoViolations(
            ruleId = RULE,
            rule = "every `*Route` navigation key must implement `androidx.navigation3.runtime.NavKey`.",
            fix = "add `: NavKey` (and `@Serializable`), or rename if it is not a navigation key.",
            offenders = offenders,
        )
    }

    @Test
    fun `K5 - repository interfaces live in domain, implementations in data`() {
        val featureFiles = ArchScope.featureFiles()

        val interfacesOutsideDomain =
            featureFiles
                .flatMap { it.interfaces(includeNested = true) }
                .filter { it.name.endsWith("Repository") }
                .filterNot { it.packageName.contains(".domain") }
                .map { "${it.qualified()} — Repository interface outside ..domain.." }

        val implsOutsideData =
            featureFiles
                .flatMap { it.classes(includeNested = true) }
                .filter { it.name.endsWith("Repository") || it.name.endsWith("RepositoryImpl") }
                .filterNot { it.packageName.contains(".data") }
                .map { "${it.qualified()} — Repository implementation outside ..data.." }

        assertNoViolations(
            ruleId = RULE,
            rule = "`*Repository` interfaces belong in `..domain..`; their implementations belong in `..data..`.",
            fix = "move the declaration into the correct layer package.",
            offenders = interfacesOutsideDomain + implsOutsideData,
        )
    }

    @Test
    fun `K5 - navigation modules are Hilt modules`() {
        val offenders =
            ArchScope
                .objects()
                .filter { it.name.endsWith("NavigationModule") }
                .filterNot { it.hasAnnotationWithName("Module") }
                .map { it.qualified() }

        assertNoViolations(
            ruleId = RULE,
            rule =
                "every `*NavigationModule` must be a Dagger/Hilt `@Module` — it provides " +
                    "`@Provides @IntoSet EntryProviderInstaller`, the one seam a feature exposes to the host.",
            fix = "annotate the object with `@Module @InstallIn(...)`.",
            offenders = offenders,
        )
    }
}
