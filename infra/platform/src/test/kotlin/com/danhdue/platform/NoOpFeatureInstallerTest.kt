/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.platform

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Behaviour of [NoOpFeatureInstaller] — the JVM/test [FeatureInstaller]. */
class NoOpFeatureInstallerTest {
    private val installer = NoOpFeatureInstaller()

    @Test
    fun `ensureInstalled runs onReady synchronously before returning`() =
        runTest {
            var readyBeforeReturn = false

            installer.ensureInstalled(module = "scanner") { readyBeforeReturn = true }

            assertTrue(readyBeforeReturn)
        }

    @Test
    fun `ensureInstalled runs onReady exactly once`() =
        runTest {
            var invocations = 0

            installer.ensureInstalled(module = "scanner") { invocations++ }

            assertEquals(1, invocations)
        }

    @Test
    fun `ensureInstalled ignores the module name and always reports ready`() =
        runTest {
            val readyModules = mutableListOf<String>()

            listOf("scanner", "", "unknown-module").forEach { module ->
                installer.ensureInstalled(module) { readyModules += module }
            }

            assertEquals(listOf("scanner", "", "unknown-module"), readyModules)
        }
}
