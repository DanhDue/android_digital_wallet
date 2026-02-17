/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.framework.network.calladapter

import com.danhdue.framework.network.DataState

/**
 * Extension functions to bridge [NetworkResponse] to [DataState].
 *
 * These utilities reduce repository boilerplate by providing a single-line
 * conversion from network layer results to domain layer results.
 */

/**
 * Converts this [NetworkResponse] to a [DataState] with the same success type.
 *
 * Example:
 * ```kotlin
 * val dataState: DataState<UserDto> = networkResponse.toDataState()
 * ```
 */
fun <T> NetworkResponse<T>.toDataState(): DataState<T> =
    fold(
        onSuccess = { DataState.Success(it) },
        onError = { DataState.Error(it) },
    )

/**
 * Converts this [NetworkResponse] to a [DataState] with a transformed success type.
 *
 * This is the primary utility for repositories to convert API responses to domain models
 * in a single line.
 *
 * Example:
 * ```kotlin
 * // Before: 30+ lines of when expression
 * // After:
 * return remoteDataSource.login(request).toDataState { it.toDomain() }
 * ```
 *
 * @param transform Function to transform the success body to the desired domain type.
 * @return [DataState.Success] with transformed value, or [DataState.Error] with exception.
 */
inline fun <T, R> NetworkResponse<T>.toDataState(crossinline transform: (T) -> R): DataState<R> =
    fold(
        onSuccess = { DataState.Success(transform(it)) },
        onError = { DataState.Error(it) },
    )

/**
 * Converts this [NetworkResponse] to a [DataState] with a suspend transformation.
 *
 * Use this when the transformation involves suspend functions (e.g., database caching).
 *
 * Example:
 * ```kotlin
 * return remoteDataSource.getUser(id).toDataStateSuspend { dto ->
 *     localDataSource.cache(dto)
 *     dto.toDomain()
 * }
 * ```
 *
 * @param transform Suspend function to transform the success body.
 * @return [DataState.Success] with transformed value, or [DataState.Error] with exception.
 */
public suspend inline fun <T, R> NetworkResponse<T>.toDataStateSuspend(crossinline transform: suspend (T) -> R): DataState<R> =
    when (this) {
        is NetworkResponse.Success -> DataState.Success(transform(body))
        is NetworkResponse.ApiError -> DataState.Error(ApiException(code, body?.toString()))
        is NetworkResponse.NetworkError -> DataState.Error(error)
        is NetworkResponse.UnknownError -> DataState.Error(error ?: UnknownNetworkException())
    }
