/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package {{package}}.domain.usecase
import com.danhdue.framework.network.DataState
import {{package}}.domain.entities.{{name.pascalCase()}}Entity
import {{package}}.domain.repository.{{name.pascalCase()}}Repository
import javax.inject.Inject

/**
 * Use case for getting {{name.pascalCase()}} data.
 */
class Get{{name.pascalCase()}}DataUseCase
    @Inject
    constructor(
        private val repository: {{name.pascalCase()}}Repository,
    ) {
        suspend operator fun invoke(): DataState<{{name.pascalCase()}}Entity> = repository.get{{name.pascalCase()}}Data()
    }
