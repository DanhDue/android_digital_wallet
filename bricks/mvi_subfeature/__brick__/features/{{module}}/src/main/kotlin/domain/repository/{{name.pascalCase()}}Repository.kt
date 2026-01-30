/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.{{module}}.domain.repository

import com.danhdue.{{module}}.domain.entities.{{name.pascalCase()}}Entity

/**
 * Repository interface for {{name.pascalCase()}} subfeature.
 */
interface {{name.pascalCase()}}Repository {
    suspend fun get{{name.pascalCase()}}Data(): Result<{{name.pascalCase()}}Entity>
}
