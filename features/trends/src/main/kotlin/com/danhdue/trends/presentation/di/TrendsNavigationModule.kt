/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.trends.presentation.di

import com.danhdue.framework.navigation.EntryProviderInstaller
import com.danhdue.trends.presentation.TrendsRoot
import com.danhdue.trends.presentation.TrendsRoute
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object TrendsNavigationModule {
    @Provides
    @IntoSet
    fun provideTrendsEntries(): EntryProviderInstaller =
        {
            entry<TrendsRoute> {
                TrendsRoot(onEvent = {})
            }
        }
}
