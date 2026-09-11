/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package {{package}}.domain.usecase

import {{package}}.domain.model.{{name.pascalCase()}}Data
import {{package}}.domain.repository.{{name.pascalCase()}}Repository
import javax.inject.Inject

class Get{{name.pascalCase()}}DataUseCase @Inject constructor(
    private val repository: {{name.pascalCase()}}Repository
) {
    suspend fun execute(): Result<{{name.pascalCase()}}Data> {
        return repository.get{{name.pascalCase()}}Data()
    }
}
