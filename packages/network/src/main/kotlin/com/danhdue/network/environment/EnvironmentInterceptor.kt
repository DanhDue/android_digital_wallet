/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.network.environment

import okhttp3.Interceptor
import okhttp3.Response

class EnvironmentInterceptor : Interceptor {
    private val devUrl = "dev.api.com"
    private val stagingUrl = "staging.api.com"
    private val productionUrl = "production.api.com"

    @Environment
    private var env = Environment.Companion.DEVELOPMENT

    override fun intercept(chain: Interceptor.Chain): Response {
        val currentRequest = chain.request()
        val host =
            when (env) {
                Environment.Companion.DEVELOPMENT -> devUrl
                Environment.Companion.STAGING -> stagingUrl
                else -> productionUrl
            }
        return chain.proceed(
            currentRequest
                .newBuilder()
                .url(
                    currentRequest
                        .url
                        .newBuilder()
                        .host(host)
                        .build(),
                ).build(),
        )
    }

    /**
     * EnvironmentInterceptor inject edip setEnvironment çağrılır.
     */
    fun setEnvironment(
        @Environment type: Int,
        action: () -> Unit,
    ) {
        env = type
        action.invoke()
    }
}
