/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package {{package}}.domain.usecase

import {{package}}.domain.repository.{{name.pascalCase()}}Repository
import javax.inject.Inject

class Sync{{name.pascalCase()}}DataUseCase @Inject constructor(
    private val repository: {{name.pascalCase()}}Repository
) {
    suspend fun execute(): Result<Boolean> {
        return repository.syncData()
    }
}
