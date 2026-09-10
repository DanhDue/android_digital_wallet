/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.konsist

import com.danhdue.konsist.support.Baseline
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

/**
 * Unit tests for [Baseline.load] — the parser behind the enforced-rule
 * suppression file. Covers rule grouping, `//` and `#` comment stripping, blank
 * lines, and the malformed-line hard error, using temp fixtures.
 */
class BaselineParserTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    private fun baselineFile(contents: String): File = tempFolder.newFile("konsist_baseline.txt").apply { writeText(contents) }

    @Test
    fun `groups fully-qualified names by rule id and strips the TODO comment`() {
        val file =
            baselineFile(
                """
                |# header comment
                |K5 com.example.sample.presentation.AlphaViewModel   // TODO(task_9): extend MviViewModel
                |K5 com.example.sample.presentation.BetaViewModel    // TODO(task_9)
                |K8 com.example.sample.presentation.GammaThing       // TODO(task_12)
                """.trimMargin(),
            )

        val parsed = Baseline.load(file)

        assertEquals(
            setOf(
                "com.example.sample.presentation.AlphaViewModel",
                "com.example.sample.presentation.BetaViewModel",
            ),
            parsed["K5"],
        )
        assertEquals(setOf("com.example.sample.presentation.GammaThing"), parsed["K8"])
    }

    @Test
    fun `ignores blank lines and full-line comments`() {
        val file =
            baselineFile(
                """
                |
                |   # indented comment
                |K5 com.danhdue.foo.BarViewModel
                |
                """.trimMargin(),
            )
        assertEquals(mapOf("K5" to setOf("com.danhdue.foo.BarViewModel")), Baseline.load(file))
    }

    @Test
    fun `a missing file yields an empty map`() {
        assertEquals(emptyMap<String, Set<String>>(), Baseline.load(File(tempFolder.root, "absent.txt")))
    }

    @Test
    fun `rejects a line that is not RuleId plus one identifier`() {
        val file = baselineFile("K5 com.danhdue.foo.Bar extra-token")
        val error = assertThrows(IllegalArgumentException::class.java) { Baseline.load(file) }
        assertTrue(error.message.orEmpty().contains("line 1"))
    }

    @Test
    fun `the checked-in baseline only references enforced rules`() {
        // All Konsist rules are now enforced (no @Ignore). K1 was enabled in Task 12, K10 in Task 13.
        val enforced = setOf("K1", "K2", "K3", "K4", "K5", "K6", "K7", "K8", "K9", "K10")
        val stray =
            Baseline
                .load(
                    com
                        .danhdue
                        .konsist
                        .support
                        .RepoRoot
                        .resolve("konsist-test/konsist_baseline.txt"),
                ).keys - enforced
        assertTrue("Baseline references non-enforced rules: $stray", stray.isEmpty())
    }
}
