/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.konsist.support

import java.io.File

/** A directed, temporarily-allowed cross-feature edge, `from → to`. */
data class FeatureEdge(
    val from: String,
    val to: String,
)

/**
 * Parser for `scripts/konsist_boundary_whitelist.txt` (Konsist rule K1).
 *
 * Grammar (mirrors the Flutter `module_boundary_whitelist.txt`):
 * - one entry per line: `<from>` `→` `<to>` (arrow = `U+2192`);
 * - blank lines and lines whose first non-blank character is `#` are ignored;
 * - leading / trailing whitespace on the line and around each side is trimmed;
 * - `\r` (CRLF input) is treated as whitespace and trimmed.
 *
 * A line that is neither blank nor a comment and does not contain exactly one
 * arrow with non-empty sides is a hard error: the whitelist is a checked-in
 * governance artefact and a typo must not silently disable an exception.
 */
object BoundaryWhitelist {
    const val ARROW: String = "→"

    /** Parses raw [content]; see the class doc for the grammar. */
    fun parse(content: String): Set<FeatureEdge> =
        content
            .lineSequence()
            .withIndex()
            .mapNotNull { (index, rawLine) ->
                val line = rawLine.trim()
                when {
                    line.isEmpty() -> null
                    line.startsWith("#") -> null
                    else -> parseEntry(line, lineNumber = index + 1)
                }
            }.toSet()

    /** Parses the whitelist [file]; a missing file yields an empty set. */
    fun parseFile(file: File): Set<FeatureEdge> = if (file.isFile) parse(file.readText()) else emptySet()

    private fun parseEntry(
        line: String,
        lineNumber: Int,
    ): FeatureEdge {
        val parts = line.split(ARROW)
        require(parts.size == 2) {
            "Malformed whitelist entry on line $lineNumber: \"$line\" — expected exactly one '$ARROW' " +
                "separating a source and a target feature, e.g. `home${ARROW}settings`."
        }
        val from = parts[0].trim()
        val to = parts[1].trim()
        require(from.isNotEmpty() && to.isNotEmpty()) {
            "Malformed whitelist entry on line $lineNumber: \"$line\" — both the source and the " +
                "target feature name must be non-empty."
        }
        return FeatureEdge(from, to)
    }
}
