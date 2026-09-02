/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.konsist.support

/**
 * Reads `:app`'s `android.dynamicFeatures` list from `app/build.gradle.kts`.
 *
 * A Dynamic Feature Module is forced to depend on `:app` (the dependency is
 * inverted), so Konsist rule K8 ("a feature must not import host-internal
 * `com.danhdue.androiddigitalwallet.*`") must exempt exactly those modules —
 * read from the build script, never hand-maintained (epic design §4.4, §6.1).
 *
 * Today the list is empty (`scanner` becomes a DFM only in Task 14); this parser
 * exists so K8 needs no change when it does.
 */
object DfmModules {
    private val DYNAMIC_FEATURES_ENTRY = Regex(""""(:features:[A-Za-z0-9_]+)"""")

    /** `:features:*` Gradle paths declared as dynamic feature modules of `:app`. */
    fun modulePaths(): Set<String> {
        val buildFile = RepoRoot.resolve("app/build.gradle.kts")
        if (!buildFile.isFile) return emptySet()
        val text = buildFile.readText()
        val declaresDynamicFeatures =
            text.contains("dynamicFeatures") &&
                Regex("""dynamicFeatures\s*(=|\+=|\.set)""").containsMatchIn(text)
        if (!declaresDynamicFeatures) return emptySet()
        return DYNAMIC_FEATURES_ENTRY
            .findAll(text)
            .map { it.groupValues[1] }
            .toSet()
    }

    /** The Kotlin package prefixes owned by the DFM modules (for import matching). */
    fun exemptPackagePrefixes(): Set<String> =
        modulePaths()
            .mapNotNull { path -> Feature.entries.firstOrNull { ":features:${it.moduleName}" == path } }
            .map { it.packagePrefix }
            .toSet()
}
