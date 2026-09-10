/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package {{package}}.presentation.di

import com.danhdue.platform.AppRoutes
import com.danhdue.platform.deeplink.DeepLink
import com.danhdue.platform.deeplink.DeepLinkResolver
import com.danhdue.platform.deeplink.DeepLinkTarget
import javax.inject.Inject

/**
 * Deep link resolver for the {{name.snakeCase()}} feature.
 *
 * Resolves URIs under the `{{name.snakeCase()}}` feature:
 * - `myapp://{{name.snakeCase()}}` -> [AppRoutes.{{name.pascalCase()}}Route]
 */
class {{name.pascalCase()}}DeepLinkResolver @Inject constructor() : DeepLinkResolver {
    override fun resolve(link: DeepLink): DeepLinkTarget? {
        if (link.feature != FEATURE) return null

        return when (link.segments) {
            emptyList<String>() -> DeepLinkTarget(destination = AppRoutes.{{name.pascalCase()}}Route)
            else -> null
        }
    }

    companion object {
        private const val FEATURE = "{{name.snakeCase()}}"
    }
}
