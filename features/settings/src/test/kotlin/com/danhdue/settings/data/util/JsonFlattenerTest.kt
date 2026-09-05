/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.data.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class JsonFlattenerTest {
    @Test
    fun `flatten flattens nested map into dot-notation keys`() {
        val input =
            mapOf(
                "settings" to
                    mapOf(
                        "preferences" to
                            mapOf(
                                "darkMode" to "Dark Mode",
                                "language" to "Language",
                            ),
                    ),
                "common" to
                    mapOf(
                        "ok" to "OK",
                    ),
            )

        val flattened = JsonFlattener.flatten(input)

        assertEquals("Dark Mode", flattened["settings.preferences.darkMode"])
        assertEquals("Language", flattened["settings.preferences.language"])
        assertEquals("OK", flattened["common.ok"])
        assertEquals(3, flattened.size)
    }

    @Test
    fun `flatten handles primitive types by converting to string`() {
        val input =
            mapOf(
                "config" to
                    mapOf(
                        "version" to 1,
                        "isBeta" to false,
                        "ratio" to 2.5,
                    ),
            )

        val flattened = JsonFlattener.flatten(input)

        assertEquals("1", flattened["config.version"])
        assertEquals("false", flattened["config.isBeta"])
        assertEquals("2.5", flattened["config.ratio"])
    }

    @Test
    fun `flatten returns empty map when input is empty`() {
        val flattened = JsonFlattener.flatten(emptyMap())
        assertTrue(flattened.isEmpty())
    }
}
