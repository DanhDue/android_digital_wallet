/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.konsist.support

/**
 * Catalogue of the repository's feature modules and the Kotlin package each one
 * owns.
 *
 * The package prefix is **not** always the module directory name (the template's
 * two features happen to match, but historically `features/myWallet` →
 * `com.danhdue.wallet`), so boundary rules must translate between the two.
 * [moduleName] matches the `:features:<name>` Gradle path segment and the
 * identifiers used in `scripts/konsist_boundary_whitelist.txt`.
 *
 * Task 13 stripped the repo to the reusable template: only `settings` (real) and
 * `scanner` (empty; becomes a dynamic-feature module in Task 14) remain.
 *
 * Derived from each module's `android.namespace` in `features/<name>/build.gradle.kts`.
 */
enum class Feature(
    val moduleName: String,
    val packagePrefix: String,
) {
    SCANNER("scanner", "com.danhdue.scanner"),
    SETTINGS("settings", "com.danhdue.settings"),
    ;

    companion object {
        /**
         * The feature that owns [qualifiedName] (a package name or a fully
         * qualified declaration / import name), or `null` if it belongs to no
         * feature (packages, host, third-party).
         */
        fun owning(qualifiedName: String?): Feature? {
            if (qualifiedName.isNullOrBlank()) return null
            return entries.firstOrNull { feature ->
                qualifiedName == feature.packagePrefix ||
                    qualifiedName.startsWith(feature.packagePrefix + ".") ||
                    qualifiedName == "com.danhdue.features.${feature.moduleName}.sample" ||
                    qualifiedName.startsWith("com.danhdue.features.${feature.moduleName}.sample.")
            }
        }
    }
}
