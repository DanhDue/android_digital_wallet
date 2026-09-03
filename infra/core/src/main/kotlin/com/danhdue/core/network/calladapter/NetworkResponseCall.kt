/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.core.network.calladapter

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

internal class NetworkResponseCall<S : Any>(
    private val delegate: Call<S>,
    private val errorConverter: retrofit2.Converter<okhttp3.ResponseBody, Any>,
) : Call<NetworkResponse<S>> {
    override fun enqueue(callback: Callback<NetworkResponse<S>>) =
        delegate.enqueue(
            object : Callback<S> {
                override fun onResponse(
                    call: Call<S>,
                    response: Response<S>,
                ) {
                    val body = response.body()
                    val code = response.code()
                    val error = response.errorBody()

                    if (response.isSuccessful) {
                        if (body != null) {
                            callback.onResponse(
                                this@NetworkResponseCall,
                                Response.success(NetworkResponse.Success(body)),
                            )
                        } else {
                            // Response is successful but the body is null
                            callback.onResponse(
                                this@NetworkResponseCall,
                                Response.success(NetworkResponse.UnknownError(null)),
                            )
                        }
                    } else {
                        val errorBody =
                            when {
                                error == null -> null
                                error.contentLength() == 0L -> null
                                else ->
                                    try {
                                        errorConverter.convert(error)
                                    } catch (_: Exception) {
                                        null
                                    }
                            }
                        callback.onResponse(
                            this@NetworkResponseCall,
                            Response.success(NetworkResponse.ApiError(errorBody, code)),
                        )
                    }
                }

                override fun onFailure(
                    call: Call<S>,
                    t: Throwable,
                ) {
                    val networkResponse =
                        when (t) {
                            is IOException -> NetworkResponse.NetworkError(t)
                            else -> NetworkResponse.UnknownError(t)
                        }
                    callback.onResponse(
                        this@NetworkResponseCall,
                        Response.success(networkResponse),
                    )
                }
            },
        )

    override fun isExecuted() = delegate.isExecuted

    override fun clone() = NetworkResponseCall(delegate.clone(), errorConverter)

    override fun isCanceled() = delegate.isCanceled

    override fun cancel() = delegate.cancel()

    override fun execute(): Response<NetworkResponse<S>> =
        throw UnsupportedOperationException(
            "NetworkResponseCall doesn't support execute",
        )

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()
}
