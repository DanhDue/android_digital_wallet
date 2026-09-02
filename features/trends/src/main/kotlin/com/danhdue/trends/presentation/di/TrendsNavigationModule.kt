/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.trends.presentation.di

import com.danhdue.platform.AppRoutes
import com.danhdue.platform.EntryProviderInstaller
import com.danhdue.trends.presentation.TrendsRoot
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
            entry<AppRoutes.TrendsRoute> {
                TrendsRoot(onEvent = {})
            }
        }
}
