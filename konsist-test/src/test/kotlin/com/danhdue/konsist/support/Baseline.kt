/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.konsist.support

import java.io.File

/**
 * Parser + lookup for `konsist-test/konsist_baseline.txt` — the set of
 * pre-existing violations an **enforced** rule tolerates until its cleanup task.
 *
 * Grammar:
 * - one entry per line: `<RuleId> <fully-qualified declaration>`;
 * - an inline `//` comment (the `// TODO(task_N): …` marker) is stripped;
 * - blank lines and lines starting with `#` are ignored;
 * - a non-blank, non-comment line that is not `<token> <token>` is a hard error.
 */
object Baseline {
    private val entriesByRule: Map<String, Set<String>> by lazy {
        load(RepoRoot.resolve("konsist-test/konsist_baseline.txt"))
    }

    /** Fully-qualified declaration names baselined for [ruleId] (e.g. `"K5"`). */
    fun suppressedFor(ruleId: String): Set<String> = entriesByRule[ruleId].orEmpty()

    internal fun load(file: File): Map<String, Set<String>> {
        if (!file.isFile) return emptyMap()
        return file
            .readLines()
            .withIndex()
            .mapNotNull { (index, rawLine) ->
                val line = rawLine.substringBefore("//").trim()
                when {
                    line.isEmpty() -> null
                    line.startsWith("#") -> null
                    else -> parseEntry(line, lineNumber = index + 1)
                }
            }.groupBy({ it.first }, { it.second })
            .mapValues { (_, names) -> names.toSet() }
    }

    private fun parseEntry(
        line: String,
        lineNumber: Int,
    ): Pair<String, String> {
        val tokens = line.split(Regex("\\s+"))
        require(tokens.size == 2) {
            "Malformed baseline entry on line $lineNumber: \"$line\" — expected " +
                "`<RuleId> <fully.qualified.Name>` (plus an optional `// TODO(task_N)` comment)."
        }
        return tokens[0] to tokens[1]
    }
}
