/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.{{module}}.data.repository

import com.danhdue.core.network.DataState
import com.danhdue.{{module}}.domain.entities.{{name.pascalCase()}}Entity
import com.danhdue.{{module}}.domain.repository.{{name.pascalCase()}}Repository
import javax.inject.Inject

/**
 * Implementation of [{{name.pascalCase()}}Repository].
 */
class {{name.pascalCase()}}RepositoryImpl
    @Inject
    constructor() : {{name.pascalCase()}}Repository {
        override suspend fun get{{name.pascalCase()}}Data(): DataState<{{name.pascalCase()}}Entity> {
            return DataState.Success({{name.pascalCase()}}Entity())
        }
    }
