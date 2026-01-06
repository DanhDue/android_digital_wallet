/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.libraries.testutils

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.InputStreamReader

/**
 * Base class for service test
 */
abstract class BaseServiceTest<T : Any> : MockkUnitTest() {
    protected lateinit var mockWebServer: MockWebServer
    protected lateinit var service: T

    @Before
    override fun setUp() {
        super.setUp()
        mockWebServer = MockWebServer()
        mockWebServer.start()

        service =
            Retrofit
                .Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(getServiceClass())
    }

    @After
    override fun tearDown() {
        super.tearDown()
        mockWebServer.shutdown()
    }

    /**
     * Use to get service class
     * @return service class
     */
    abstract fun getServiceClass(): Class<T>

    /**
     * Enqueue response from file
     * @param fileName name of json file in test resources
     * @param responseCode response code
     */
    fun enqueueResponse(
        fileName: String,
        responseCode: Int = 200,
    ) {
        val inputStream = javaClass.classLoader?.getResourceAsStream(fileName)
        val reader = InputStreamReader(inputStream)
        val content = reader.readText()
        val response =
            MockResponse()
                .setResponseCode(responseCode)
                .setBody(content)
        mockWebServer.enqueue(response)
    }
}
