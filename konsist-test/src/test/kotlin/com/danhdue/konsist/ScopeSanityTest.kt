/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.konsist

import com.danhdue.konsist.support.ArchScope
import com.danhdue.konsist.support.Feature
import com.danhdue.konsist.support.packageName
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Guards the [ArchScope] itself: if the Konsist scope silently resolved to
 * nothing, every rule above would vacuously "pass". These tests make an empty or
 * mis-filtered scope a hard failure.
 */
class ScopeSanityTest {
    @Test
    fun `production scope is non-empty`() {
        // Threshold lowered in Task 13 after the five wallet-domain features were deleted
        // (the trimmed template carries ~180 production Kotlin files). Its job is to make a
        // silently-empty or mis-filtered scope a hard failure, not to track the exact count.
        assertTrue("Konsist resolved zero production files", ArchScope.mainFiles.size > 60)
    }

    @Test
    fun `production scope excludes build, bricks, buildSrc and the konsist-test module`() {
        val paths = ArchScope.mainFiles.map { it.path.replace('\\', '/') }
        assertTrue("bricks/ leaked into scope", paths.none { it.contains("/bricks/") })
        assertTrue("build/ leaked into scope", paths.none { it.contains("/build/") })
        assertTrue("buildSrc/ leaked into scope", paths.none { it.contains("/buildSrc/") })
        assertTrue("konsist-test/ leaked into scope", paths.none { it.contains("/konsist-test/") })
    }

    @Test
    fun `production scope excludes non-main source sets`() {
        val paths = ArchScope.mainFiles.map { it.path.replace('\\', '/') }
        assertTrue("test sources leaked into scope", paths.all { it.contains("/src/main/") })
    }

    @Test
    fun `every feature module is represented in the scope`() {
        val seen = ArchScope.featureFiles().mapNotNull { Feature.owning(it.packageName) }.toSet()
        assertEquals(Feature.entries.toSet(), seen)
    }

    @Test
    fun `the framework MVI base class is visible to the scope`() {
        // K5 relies on being able to see `MviViewModel` as a parent name.
        assertTrue(
            "MviViewModel not found — parent resolution for K5 would be unreliable",
            ArchScope.classes().any { it.name == "MviViewModel" },
        )
    }
}
