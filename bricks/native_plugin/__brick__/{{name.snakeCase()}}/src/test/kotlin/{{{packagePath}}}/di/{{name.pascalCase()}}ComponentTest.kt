/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package {{package}}.di

import android.content.Context
import io.mockk.mockk
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

class {{name.pascalCase()}}ComponentTest {

    private val mockContext = mockk<Context>(relaxed = true)

    @Before
    fun setUp() {
        {{name.pascalCase()}}ComponentProvider.reset()
    }

    @After
    fun tearDown() {
        {{name.pascalCase()}}ComponentProvider.reset()
    }

    @Test
    fun `get returns same instance for multiple invocations`() {
        val first = {{name.pascalCase()}}ComponentProvider.get(mockContext)
        val second = {{name.pascalCase()}}ComponentProvider.get(mockContext)

        assertNotNull(first)
        assertSame(first, second)
    }
}
