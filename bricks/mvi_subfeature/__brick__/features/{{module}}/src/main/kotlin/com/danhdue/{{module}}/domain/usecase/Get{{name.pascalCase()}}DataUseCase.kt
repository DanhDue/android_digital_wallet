/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.{{module}}.domain.usecase

import com.danhdue.framework.network.DataState
import com.danhdue.{{module}}.domain.entities.{{name.pascalCase()}}Entity
import com.danhdue.{{module}}.domain.repository.{{name.pascalCase()}}Repository
import javax.inject.Inject

/**
 * Use case for fetching {{name.pascalCase()}} data.
 */
class Get{{name.pascalCase()}}DataUseCase
    @Inject
    constructor(
        private val repository: {{name.pascalCase()}}Repository,
    ) {
        suspend operator fun invoke(): DataState<{{name.pascalCase()}}Entity> {
            return repository.get{{name.pascalCase()}}Data()
        }
    }
