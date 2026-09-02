/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.konsist.support

/**
 * Catalogue of the repository's feature modules and the Kotlin package each one
 * owns.
 *
 * The package prefix is **not** always the module directory name (e.g.
 * `features/myWallet` → `com.danhdue.wallet`,
 * `features/transactions` → `com.danhdue.transactions`), so boundary rules must
 * translate between the two. [moduleName] matches the `:features:<name>` Gradle
 * path segment and the identifiers used in
 * `scripts/konsist_boundary_whitelist.txt`.
 *
 * Derived from each module's `android.namespace` in `features/<name>/build.gradle.kts`.
 */
enum class Feature(
    val moduleName: String,
    val packagePrefix: String,
) {
    AUTHENTICATION("authentication", "com.danhdue.authentication"),
    HOME("home", "com.danhdue.home"),
    MY_WALLET("myWallet", "com.danhdue.wallet"),
    SCANNER("scanner", "com.danhdue.scanner"),
    SETTINGS("settings", "com.danhdue.settings"),
    SPLASH("splash", "com.danhdue.splash"),
    TRANSACTIONS("transactions", "com.danhdue.transactions"),
    TRENDS("trends", "com.danhdue.trends"),
    ;

    companion object {
        /**
         * The feature that owns [qualifiedName] (a package name or a fully
         * qualified declaration / import name), or `null` if it belongs to no
         * feature (infrastructure, host, third-party).
         */
        fun owning(qualifiedName: String?): Feature? {
            if (qualifiedName.isNullOrBlank()) return null
            return entries.firstOrNull { feature ->
                qualifiedName == feature.packagePrefix ||
                    qualifiedName.startsWith(feature.packagePrefix + ".")
            }
        }
    }
}
