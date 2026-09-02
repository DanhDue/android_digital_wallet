/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.konsist.support

import org.junit.Assert.assertTrue

/**
 * Fails the current test iff [offenders] is non-empty, with a message that names
 * the rule, the concrete fix, and every offending declaration — so a CI failure
 * is actionable without opening the rule source (controller ruling: assert
 * violation *messages*, not a bare boolean).
 */
fun assertNoViolations(
    ruleId: String,
    rule: String,
    fix: String,
    offenders: Collection<String>,
) {
    val message =
        buildString {
            append('[')
                .append(ruleId)
                .append("] ")
                .append(rule)
                .append('\n')
            append("How to fix: ").append(fix).append('\n')
            append("Offending declarations (").append(offenders.size).append("):\n")
            offenders.sorted().forEach { append("  - ").append(it).append('\n') }
        }
    assertTrue(message, offenders.isEmpty())
}
