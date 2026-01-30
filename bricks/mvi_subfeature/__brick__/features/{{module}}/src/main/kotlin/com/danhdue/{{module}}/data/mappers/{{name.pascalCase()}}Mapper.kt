/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.{{module}}.data.mappers

import com.danhdue.{{module}}.data.models.{{name.pascalCase()}}Dto
import com.danhdue.{{module}}.domain.entities.{{name.pascalCase()}}Entity

/**
 * Mapper between {{name.pascalCase()}}Dto and {{name.pascalCase()}}Entity.
 */
fun {{name.pascalCase()}}Dto.toDomain(): {{name.pascalCase()}}Entity =
    {{name.pascalCase()}}Entity(
        id = id,
        name = name,
    )

fun {{name.pascalCase()}}Entity.toDto(): {{name.pascalCase()}}Dto =
    {{name.pascalCase()}}Dto(
        id = id,
        name = name,
    )
