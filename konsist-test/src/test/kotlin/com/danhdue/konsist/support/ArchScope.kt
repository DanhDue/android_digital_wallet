/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.konsist.support

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.declaration.KoClassDeclaration
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import com.lemonappdev.konsist.api.declaration.KoFunctionDeclaration
import com.lemonappdev.konsist.api.declaration.KoInterfaceDeclaration
import com.lemonappdev.konsist.api.declaration.KoObjectDeclaration

/**
 * Shared, pre-filtered [Konsist] scope for every architecture rule.
 *
 * Scoping decisions (epic design §6.1, controller ruling #5):
 * - `Konsist.scopeFromProject()` reads the whole repository tree.
 * - `build/`, `.gradle/` are excluded by Konsist itself.
 * - We additionally drop `bricks/` (Mason templates — contain `{{mustache}}`
 *   placeholders, not valid Kotlin), `buildSrc/`, `:konsist-test` itself,
 *   generated sources, and every non-`main` source set: the K1–K9 rules target
 *   production code only.
 */
object ArchScope {
    private val EXCLUDED_PATH_FRAGMENTS =
        listOf(
            "/build/",
            "/bricks/",
            "/buildSrc/",
            "/konsist-test/",
            "/generated/",
        )

    /** Every production (`src/main`) Kotlin file in the repo, minus the exclusions above. */
    val mainFiles: List<KoFileDeclaration> by lazy {
        Konsist
            .scopeFromProject()
            .files
            .map { it to it.path.replace('\\', '/') }
            .filter { (_, normalized) -> normalized.contains("/src/main/") }
            .filterNot { (_, normalized) -> EXCLUDED_PATH_FRAGMENTS.any { normalized.contains(it) } }
            .map { (file, _) -> file }
    }

    fun classes(): List<KoClassDeclaration> = mainFiles.flatMap { it.classes(includeNested = true) }

    fun interfaces(): List<KoInterfaceDeclaration> = mainFiles.flatMap { it.interfaces(includeNested = true) }

    fun objects(): List<KoObjectDeclaration> = mainFiles.flatMap { it.objects(includeNested = true) }

    fun functions(): List<KoFunctionDeclaration> = mainFiles.flatMap { it.functions(includeNested = true) }

    /** All files whose package belongs to a [Feature]. */
    fun featureFiles(): List<KoFileDeclaration> = mainFiles.filter { Feature.owning(it.packagee?.name) != null }

    /** The module directory name for [path] (segment immediately before `/src/`), or `""`. */
    fun moduleOf(path: String): String {
        val normalized = path.replace('\\', '/')
        val beforeSrc = normalized.substringBefore("/src/", "")
        return beforeSrc.substringAfterLast('/')
    }
}
