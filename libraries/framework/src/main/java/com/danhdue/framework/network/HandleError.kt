/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
@file:Suppress("MatchingDeclarationName")

package com.danhdue.framework.network

import com.danhdue.core.network.HttpStatusCode
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

sealed class Failure : IOException() {
    data object JsonError : Failure()

    data object UnknownError : Failure()

    data object UnknownHostError : Failure()

    data object EmptyResponse : Failure()

    data object ConnectivityError : Failure()

    data object InternetError : Failure()

    data object UnAuthorizedException : Failure()

    data object ParsingDataError : Failure()

    data object IgnorableError : Failure()

    data class TimeOutError(
        override var message: String,
    ) : Failure()

    data class ApiError(
        var code: Int = 0,
        override var message: String,
    ) : Failure()

    data class ServerError(
        var code: Int = 0,
        override var message: String,
    ) : Failure()

    data class NotFoundException(
        override var message: String,
    ) : Failure()

    data class SocketTimeoutError(
        override var message: String,
    ) : Failure()

    data class BusinessError(
        override var message: String,
        val stackTrace: String,
    ) : Failure()

    data class HttpError(
        var code: Int,
        override var message: String,
    ) : Failure()
}

@Suppress("MagicNumber")
fun Throwable.handleThrowable(): Failure =
    when (this) {
        is UnknownHostException -> Failure.ConnectivityError
        is HttpException ->
            when (code()) {
                HttpStatusCode.Unauthorized.code -> Failure.UnAuthorizedException
                HttpStatusCode.NotFound.code -> Failure.NotFoundException(message ?: "Not found")
                in 400..499 -> Failure.ApiError(code(), message ?: "Client error")
                in 500..599 -> Failure.ServerError(code(), message ?: "Server error")
                else -> Failure.HttpError(code(), message ?: "HTTP error")
            }
        is SocketTimeoutException -> Failure.SocketTimeoutError(message ?: "Timeout")
        else -> message?.let { Failure.NotFoundException(it) } ?: Failure.UnknownError
    }

/**
 * Converts an HTTP status code and optional message to a [Failure].
 * Used by NetworkResponse.ApiError to create appropriate Failure instances.
 */
@Suppress("MagicNumber")
fun httpCodeToFailure(
    code: Int,
    message: String?,
): Failure =
    when (code) {
        HttpStatusCode.Unauthorized.code -> Failure.UnAuthorizedException
        HttpStatusCode.NotFound.code -> Failure.NotFoundException(message ?: "Not found")
        in 400..499 -> Failure.ApiError(code, message ?: "Client error")
        in 500..599 -> Failure.ServerError(code, message ?: "Server error")
        else -> Failure.HttpError(code, message ?: "HTTP error")
    }
