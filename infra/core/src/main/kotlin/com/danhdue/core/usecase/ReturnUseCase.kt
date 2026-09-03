/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.core.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

abstract class ReturnUseCase<in Params, ReturnType> where ReturnType : Any {
    protected abstract fun execute(params: Params): Flow<ReturnType>

    operator fun invoke(params: Params): Flow<ReturnType> =
        execute(params)
            .flowOn(Dispatchers.IO)
}
