/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.home.presentation.di

import com.danhdue.framework.navigation.EntryProviderInstaller
import com.danhdue.framework.navigation.HomeRoute
import com.danhdue.home.presentation.HomeRoot
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object HomeNavigationModule {
    @Provides
    @IntoSet
    fun provideHomeEntries(): EntryProviderInstaller =
        {
            entry<HomeRoute> {
                HomeRoot()
            }
        }
}
