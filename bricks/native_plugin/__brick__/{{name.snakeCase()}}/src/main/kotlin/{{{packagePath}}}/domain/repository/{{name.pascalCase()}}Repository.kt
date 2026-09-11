/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package {{package}}.domain.repository

import {{package}}.domain.model.{{name.pascalCase()}}Data

interface {{name.pascalCase()}}Repository {
    suspend fun get{{name.pascalCase()}}Data(): Result<{{name.pascalCase()}}Data>
    suspend fun syncData(): Result<Boolean>
}
