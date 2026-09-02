/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.core.network.calladapter

import java.io.IOException

sealed class NetworkResponse<out T> {
    /** Represents a successful network request (2xx). */
    data class Success<T>(
        val body: T,
    ) : NetworkResponse<T>()

    /**
     * Represents a non-successful network request (e.g., 400, 401, 500).
     * @param body The error body from the response.
     * @param code The HTTP status code.
     */
    data class ApiError(
        val body: Any?,
        val code: Int,
    ) : NetworkResponse<Nothing>()

    /** Represents a network error (e.g., no internet, timeout). */
    data class NetworkError(
        val error: IOException,
    ) : NetworkResponse<Nothing>()

    /** Represents an unknown error (e.g., parsing error, other exceptions). */
    data class UnknownError(
        val error: Throwable?,
    ) : NetworkResponse<Nothing>()

    /** Map success body to another type. */
    inline fun <R> map(transform: (T) -> R): NetworkResponse<R> =
        when (this) {
            is Success -> Success(transform(body))
            is ApiError -> this
            is NetworkError -> this
            is UnknownError -> this
        }

    /** Execute action on success, returns self for chaining. */
    inline fun onSuccess(action: (T) -> Unit): NetworkResponse<T> {
        if (this is Success) action(body)
        return this
    }

    /** Execute action on any error, returns self for chaining. */
    inline fun onError(action: (Throwable) -> Unit): NetworkResponse<T> {
        when (this) {
            is ApiError -> action(ApiException(code, body?.toString()))
            is NetworkError -> action(error)
            is UnknownError -> action(error ?: UnknownNetworkException())
            is Success -> { /* no-op */ }
        }
        return this
    }

    /** Fold to a single result type by handling success and error cases. */
    inline fun <R> fold(
        onSuccess: (T) -> R,
        onError: (Throwable) -> R,
    ): R =
        when (this) {
            is Success -> onSuccess(body)
            is ApiError -> onError(ApiException(code, body?.toString()))
            is NetworkError -> onError(error)
            is UnknownError -> onError(error ?: UnknownNetworkException())
        }
}

/** Exception representing an API error with HTTP status code. */
class ApiException(
    val code: Int,
    message: String?,
) : Exception("API Error $code: $message")

/** Exception representing an unknown network error. */
class UnknownNetworkException : Exception("Unknown network error occurred")
