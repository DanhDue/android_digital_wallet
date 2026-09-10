/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package {{package}}.presentation.di

import com.danhdue.framework.navigation.LocalNestedNavigator
import com.danhdue.platform.AppRoutes
import com.danhdue.platform.EntryProviderInstaller
import com.danhdue.platform.deeplink.DeepLinkResolver
import {{package}}.presentation.{{screen.camelCase()}}.{{screen.pascalCase()}}Event
import {{package}}.presentation.{{screen.camelCase()}}.{{screen.pascalCase()}}Root
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

/**
 * Hilt module that provides navigation entries and deep link resolution for the {{name.pascalCase()}} feature.
 */
@Module
@InstallIn(SingletonComponent::class)
object {{name.pascalCase()}}NavigationModule {
    @Provides
    @IntoSet
    fun provide{{name.pascalCase()}}DeepLinkResolver(resolver: {{name.pascalCase()}}DeepLinkResolver): DeepLinkResolver = resolver

    @Provides
    @IntoSet
    fun provide{{name.pascalCase()}}Entries(): EntryProviderInstaller =
        {
            entry<AppRoutes.{{name.pascalCase()}}Route> {
                val nestedNavigator = LocalNestedNavigator.current
                {{screen.pascalCase()}}Root(
                    onEvent = { event ->
                        when (event) {
                            {{screen.pascalCase()}}Event.NavigateBack -> nestedNavigator.popBackStack()
                        }
                    },
                )
            }
        }
}
