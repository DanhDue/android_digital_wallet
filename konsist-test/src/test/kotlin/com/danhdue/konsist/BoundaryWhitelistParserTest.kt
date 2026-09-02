/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.konsist

import com.danhdue.konsist.support.BoundaryWhitelist
import com.danhdue.konsist.support.FeatureEdge
import com.danhdue.konsist.support.RepoRoot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [BoundaryWhitelist.parse] — the pure parser behind Konsist rule
 * K1. Covers the happy path plus boundary cases: blank lines, comments (with and
 * without leading whitespace), trailing whitespace, CRLF input, spaces around the
 * arrow, and every malformed shape (no arrow, empty side, multiple arrows).
 */
class BoundaryWhitelistParserTest {
    @Test
    fun `parses a single edge`() {
        assertEquals(
            setOf(FeatureEdge("home", "settings")),
            BoundaryWhitelist.parse("home→settings"),
        )
    }

    @Test
    fun `parses multiple edges across lines`() {
        val parsed = BoundaryWhitelist.parse("home→settings\nhome→trends\nauthentication→settings")
        assertEquals(
            setOf(
                FeatureEdge("home", "settings"),
                FeatureEdge("home", "trends"),
                FeatureEdge("authentication", "settings"),
            ),
            parsed,
        )
    }

    @Test
    fun `ignores blank lines and comment lines`() {
        val content =
            """
            |# module boundary whitelist
            |
            |home→settings
            |   # indented comment
            |
            |home→trends
            """.trimMargin()
        assertEquals(
            setOf(FeatureEdge("home", "settings"), FeatureEdge("home", "trends")),
            BoundaryWhitelist.parse(content),
        )
    }

    @Test
    fun `trims trailing whitespace and whitespace around the arrow`() {
        assertEquals(
            setOf(FeatureEdge("home", "settings")),
            BoundaryWhitelist.parse("   home  →  settings   "),
        )
    }

    @Test
    fun `handles CRLF line endings`() {
        val parsed = BoundaryWhitelist.parse("home→settings\r\nhome→trends\r\n")
        assertEquals(
            setOf(FeatureEdge("home", "settings"), FeatureEdge("home", "trends")),
            parsed,
        )
    }

    @Test
    fun `deduplicates repeated edges`() {
        assertEquals(
            setOf(FeatureEdge("home", "settings")),
            BoundaryWhitelist.parse("home→settings\nhome→settings"),
        )
    }

    @Test
    fun `rejects a line with no arrow`() {
        val error =
            assertThrows(IllegalArgumentException::class.java) {
                BoundaryWhitelist.parse("home settings")
            }
        assertTrue(error.message.orEmpty().contains("line 1"))
    }

    @Test
    fun `rejects an empty target`() {
        assertThrows(IllegalArgumentException::class.java) {
            BoundaryWhitelist.parse("home→")
        }
    }

    @Test
    fun `rejects an empty source`() {
        assertThrows(IllegalArgumentException::class.java) {
            BoundaryWhitelist.parse("→settings")
        }
    }

    @Test
    fun `rejects more than one arrow`() {
        assertThrows(IllegalArgumentException::class.java) {
            BoundaryWhitelist.parse("home→settings→trends")
        }
    }

    @Test
    fun `reports the offending line number`() {
        val error =
            assertThrows(IllegalArgumentException::class.java) {
                BoundaryWhitelist.parse("home→settings\n\nbroken-line")
            }
        assertTrue(error.message.orEmpty().contains("line 3"))
    }

    @Test
    fun `the checked-in whitelist is exactly the five seeded home edges`() {
        val parsed = BoundaryWhitelist.parseFile(RepoRoot.resolve("scripts/konsist_boundary_whitelist.txt"))
        assertEquals(
            setOf(
                FeatureEdge("home", "myWallet"),
                FeatureEdge("home", "transactions"),
                FeatureEdge("home", "scanner"),
                FeatureEdge("home", "trends"),
                FeatureEdge("home", "settings"),
            ),
            parsed,
        )
    }
}
