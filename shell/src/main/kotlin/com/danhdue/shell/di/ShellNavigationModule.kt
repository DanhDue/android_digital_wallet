/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.shell.di

import com.danhdue.platform.AppRoutes
import com.danhdue.platform.EntryProviderInstaller
import com.danhdue.shell.ShellRoot
import com.danhdue.shell.tabs.HomeStubPage
import com.danhdue.shell.tabs.HomeStubRoute
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
            entry<AppRoutes.ShellRoute> {
                ShellRoot()
            }
            // `:shell`-local Home tab stub — never leaves this module (see HomeStubRoute).
            entry<HomeStubRoute> {
                HomeStubPage()
            }
        }
}
