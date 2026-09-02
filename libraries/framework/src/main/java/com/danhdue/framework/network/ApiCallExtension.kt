/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.framework.network

import com.danhdue.core.network.DataState

suspend fun <T : Any> apiCall(call: suspend () -> T): DataState<T> =
    try {
        val response = call()
        DataState.Success(response)
    } catch (ex: Throwable) {
        DataState.Error(ex.handleThrowable())
    }
