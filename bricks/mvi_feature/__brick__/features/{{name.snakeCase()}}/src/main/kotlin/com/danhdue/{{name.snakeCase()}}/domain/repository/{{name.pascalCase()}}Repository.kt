/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package {{package}}.domain.repository

import com.danhdue.framework.network.DataState
import {{package}}.domain.entities.{{name.pascalCase()}}Entity

/**
 * Repository interface for {{name.pascalCase()}} feature.
 * Defines data operations contract for the domain layer.
 */
interface {{name.pascalCase()}}Repository {
    suspend fun get{{name.pascalCase()}}Data(): DataState<{{name.pascalCase()}}Entity>
}
