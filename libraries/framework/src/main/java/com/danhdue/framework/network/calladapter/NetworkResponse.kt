package com.danhdue.framework.network.calladapter

import java.io.IOException

sealed class NetworkResponse<out T> {
    /** Represents a successful network request (2xx). */
    data class Success<T>(val body: T) : NetworkResponse<T>()

    /**
     * Represents a non-successful network request (e.g., 400, 401, 500).
     * @param body The error body from the response.
     * @param code The HTTP status code.
     */
    data class ApiError(val body: Any?, val code: Int) : NetworkResponse<Nothing>()

    /** Represents a network error (e.g., no internet, timeout). */
    data class NetworkError(val error: IOException) : NetworkResponse<Nothing>()

    /** Represents an unknown error (e.g., parsing error, other exceptions). */
    data class UnknownError(val error: Throwable?) : NetworkResponse<Nothing>()
}
