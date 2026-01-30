/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package {{package}}.presentation.di

import com.danhdue.framework.navigation.EntryProviderInstaller
import com.danhdue.framework.navigation.Navigator
import {{package}}.presentation.{{screen.camelCase()}}.{{screen.pascalCase()}}Event
import {{package}}.presentation.{{screen.camelCase()}}.{{screen.pascalCase()}}Root
import {{package}}.presentation.{{screen.camelCase()}}.{{screen.pascalCase()}}Route
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

/**
 * Hilt module that provides navigation entries for the {{name.pascalCase()}} feature.
 */
@Module
@InstallIn(ActivityRetainedComponent::class)
object {{name.pascalCase()}}NavigationModule {
    @Provides
    @IntoSet
    fun provide{{name.pascalCase()}}Entries(navigator: Navigator): EntryProviderInstaller =
        {
            entry<{{screen.pascalCase()}}Route> {
                {{screen.pascalCase()}}Root(
                    onEvent = { event ->
                        when (event) {
                            {{screen.pascalCase()}}Event.NavigateBack -> navigator.popBackStack()
                        }
                    },
                )
            }
        }
}
