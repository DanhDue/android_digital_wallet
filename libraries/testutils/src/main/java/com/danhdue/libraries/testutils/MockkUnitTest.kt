package com.danhdue.libraries.testutils

import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Before
import org.junit.Rule

open class MockkUnitTest {
    open fun onCreate() {}

    open fun onDestroy() {}

    @get:Rule
    var testCoroutineRule = TestCoroutineRule()

    @Before
    open fun setUp() {
        MockKAnnotations.init(this)
        onCreate()
    }

    @After
    open fun tearDown() {
        onDestroy()
        unmockkAll()
        clearAllMocks()
    }
}
