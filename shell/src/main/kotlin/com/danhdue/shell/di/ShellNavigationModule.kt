/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.shell.di

import com.danhdue.framework.navigation.ShellRoute
import com.danhdue.platform.EntryProviderInstaller
import com.danhdue.shell.ShellRoot
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object ShellNavigationModule {
    @Provides
    @IntoSet
    fun provideShellEntries(): EntryProviderInstaller =
        {
            entry<ShellRoute> {
                ShellRoot()
            }
        }
}
