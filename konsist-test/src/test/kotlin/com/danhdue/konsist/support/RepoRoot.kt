/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.konsist.support

import java.io.File

/**
 * Locates the repository root so rule tests can read checked-in gate files
 * (`scripts/konsist_boundary_whitelist.txt`, `konsist-test/konsist_baseline.txt`)
 * regardless of whether the test JVM is started by Gradle (working dir =
 * `:konsist-test` project dir) or by an IDE.
 */
object RepoRoot {
    /** The first ancestor directory that contains a `settings.gradle.kts`. */
    val dir: File by lazy {
        generateSequence(File(System.getProperty("user.dir")).absoluteFile) { it.parentFile }
            .firstOrNull { File(it, "settings.gradle.kts").isFile }
            ?: error(
                "Could not locate the repository root (no settings.gradle.kts found walking up " +
                    "from ${System.getProperty("user.dir")}).",
            )
    }

    /** Resolves [relativePath] against the repository root. */
    fun resolve(relativePath: String): File = File(dir, relativePath)
}
