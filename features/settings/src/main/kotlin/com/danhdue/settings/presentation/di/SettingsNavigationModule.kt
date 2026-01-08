/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.presentation.di

import com.danhdue.framework.navigation.EntryProviderInstaller
import com.danhdue.settings.presentation.SettingsRoot
import com.danhdue.settings.presentation.SettingsRoute
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object SettingsNavigationModule {
    @Provides
    @IntoSet
    fun provideSettingsEntries(): EntryProviderInstaller =
        {
            entry<SettingsRoute> {
                SettingsRoot(onEvent = {})
            }
        }
}
