/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package {{package}}.data.mappers

import {{package}}.data.models.{{name.pascalCase()}}Dto
import {{package}}.domain.entities.{{name.pascalCase()}}Entity

/**
 * Mapper between data layer DTOs and domain entities.
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
