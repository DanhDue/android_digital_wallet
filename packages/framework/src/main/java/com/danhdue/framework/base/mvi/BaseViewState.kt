/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.framework.base.mvi

sealed interface BaseViewState<out T> {
    object Loading : BaseViewState<Nothing>

    object Empty : BaseViewState<Nothing>

    data class Data<T>(
        val value: T,
    ) : BaseViewState<T>

    data class Error(
        val throwable: Throwable,
    ) : BaseViewState<Nothing>
}
