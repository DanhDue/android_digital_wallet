/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.konsist

import com.danhdue.konsist.support.ArchScope
import com.danhdue.konsist.support.Feature
import com.danhdue.konsist.support.assertNoViolations
import com.danhdue.konsist.support.packageName
import org.junit.Ignore
import org.junit.Test

/**
 * Intra-feature layering rules — **K2** (`Presentation → Domain ← Data`),
 * **K3** (Domain is pure Kotlin), **K4** (`data` layer export discipline).
 * Epic design §4.3, §6.1.
 *
 * All three are deferred in Phase 0 (`@Ignore`). Bodies are complete; the
 * enabling task only removes the annotation.
 * - K2 → Task 9 (layer rules enabled with the god-module split). Design §9 Phase 1.
 * - K3 → Task 9 (same).
 * - K4 → Task 11 (export discipline, with the settings pilot). Design §9 Phase 2.
 */
class LayerRulesTest {
    private fun featureFilesInLayer(layerInfix: String) = ArchScope.featureFiles().filter { it.packageName.contains(layerInfix) }

    private fun danhdueImports(qualifiedNames: List<Pair<String, String>>) =
        qualifiedNames.filter { (_, import) -> import.startsWith("com.danhdue.") }

    @Test
    @Ignore("Phase 0: deferred — enforced in Task 9 (design §9 Phase 1).")
    fun `K2 - presentation must not import data, domain must not import presentation or data`() {
        val presentationToData =
            featureFilesInLayer(".presentation")
                .flatMap { file -> file.imports.map { file.path to it.name } }
                .let(::danhdueImports)
                .filter { (_, import) -> import.contains(".data.") }
                .map { (path, import) -> "$path: presentation imports data ($import)" }

        val domainToUpper =
            featureFilesInLayer(".domain")
                .flatMap { file -> file.imports.map { file.path to it.name } }
                .let(::danhdueImports)
                .filter { (_, import) -> import.contains(".data.") || import.contains(".presentation.") }
                .map { (path, import) -> "$path: domain imports ${if (import.contains(".data.")) "data" else "presentation"} ($import)" }

        assertNoViolations(
            ruleId = "K2",
            rule =
                "within a feature: `presentation` must not import `data`; `domain` must not import " +
                    "`presentation` or `data` (`Presentation → Domain ← Data`).",
            fix = "depend on the domain abstraction instead of the concrete layer; inject implementations via Hilt.",
            offenders = presentationToData + domainToUpper,
        )
    }

    @Test
    @Ignore("Phase 0: deferred — enforced in Task 9 (design §9 Phase 1).")
    fun `K3 - domain is pure Kotlin (no android or androidx imports)`() {
        val offenders =
            featureFilesInLayer(".domain")
                .flatMap { file -> file.imports.map { file.path to it.name } }
                .filter { (_, import) -> import.startsWith("android.") || import.startsWith("androidx.") }
                .map { (path, import) -> "$path: domain imports platform type $import" }

        assertNoViolations(
            ruleId = "K3",
            rule = "a feature's `domain` layer must not import `android.*` / `androidx.*` — it is pure Kotlin.",
            fix = "keep framework types out of domain; map them at the data or presentation boundary.",
            offenders = offenders,
        )
    }

    @Test
    @Ignore("Phase 0: deferred — enforced in Task 11 (export discipline, design §9 Phase 2).")
    fun `K4 - top-level declarations in a feature data layer are internal`() {
        val offenders =
            ArchScope
                .featureFiles()
                .filter { Feature.owning(it.packageName) != null && it.packageName.contains(".data") }
                .flatMap { file ->
                    listOf(file.classes(), file.interfaces(), file.objects())
                        .flatten()
                        .map { file.path to it }
                }.filterNot { (_, declaration) -> declaration.hasInternalModifier }
                .filterNot { (_, declaration) -> declaration.hasPrivateModifier }
                .map { (path, declaration) -> "$path: `${declaration.name}` is ${visibilityOf(declaration)} (must be internal)" }

        assertNoViolations(
            ruleId = "K4",
            rule =
                "top-level declarations under a feature's `..data..` package must be `internal` " +
                    "(default `public` leaks DTOs / repository impls out of the feature).",
            fix = "add the `internal` modifier; only `domain` and `presentation` types are part of a feature's API.",
            offenders = offenders,
        )
    }

    private fun visibilityOf(declaration: com.lemonappdev.konsist.api.provider.modifier.KoVisibilityModifierProvider): String =
        when {
            declaration.hasPublicModifier -> "public"
            declaration.hasProtectedModifier -> "protected"
            declaration.hasInternalModifier -> "internal"
            declaration.hasPrivateModifier -> "private"
            else -> "public (implicit)"
        }
}
