/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.network.interceptor

import app.cash.turbine.test
import com.danhdue.platform.AppEvent
import com.danhdue.platform.AppEventBus
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Task 11: an unrecovered `401` on the final response publishes
 * [AppEvent.UserLoggedOut] on the [AppEventBus]; any other status is silent.
 */
class UnauthorizedInterceptorTest {
    private val request: Request = Request.Builder().url("https://example.com/resource").build()

    private fun chainReturning(code: Int): Interceptor.Chain {
        val response =
            Response
                .Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(code)
                .message(if (code == 401) "Unauthorized" else "OK")
                .body("".toResponseBody())
                .build()
        return mockk {
            every { request() } returns request
            every { proceed(any()) } returns response
        }
    }

    @Test
    fun `publishes UserLoggedOut once when the final response is 401`() =
        runTest(UnconfinedTestDispatcher()) {
            val bus = AppEventBus()
            val interceptor = UnauthorizedInterceptor(bus)

            bus.on<AppEvent.UserLoggedOut>().test {
                interceptor.intercept(chainReturning(code = 401))

                assertEquals(AppEvent.UserLoggedOut, awaitItem())
                expectNoEvents()
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `does not publish anything when the response is 200`() =
        runTest(UnconfinedTestDispatcher()) {
            val bus = AppEventBus()
            val interceptor = UnauthorizedInterceptor(bus)

            bus.on<AppEvent>().test {
                interceptor.intercept(chainReturning(code = 200))

                expectNoEvents()
                cancelAndConsumeRemainingEvents()
            }
        }
}
