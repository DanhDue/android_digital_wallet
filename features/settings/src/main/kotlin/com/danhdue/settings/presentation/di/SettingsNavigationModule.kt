/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.presentation.di

import com.danhdue.framework.navigation.EntryProviderInstaller
import com.danhdue.framework.navigation.LocalNestedNavigator
import com.danhdue.framework.navigation.LoginRoute
import com.danhdue.framework.navigation.Navigator
import com.danhdue.settings.presentation.SettingsEvent
import com.danhdue.settings.presentation.SettingsRoot
import com.danhdue.settings.presentation.SettingsRoute
import com.danhdue.settings.presentation.profile.ProfileEvent
import com.danhdue.settings.presentation.profile.ProfileRoot
import com.danhdue.settings.presentation.profile.ProfileRoute
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
    fun provideSettingsEntries(navigator: Navigator): EntryProviderInstaller =
        {
            entry<SettingsRoute> {
                val nestedNavigator = LocalNestedNavigator.current
                SettingsRoot(
                    onEvent = { event ->
                        when (event) {
                            SettingsEvent.NavigateToProfile -> nestedNavigator.navigate(ProfileRoute)
                            SettingsEvent.NavigateToLogin -> navigator.navigateAndClearBackStack(LoginRoute)
                        }
                    },
                )
            }
            entry<ProfileRoute> {
                val nestedNavigator = LocalNestedNavigator.current
                ProfileRoot(
                    onEvent = { event ->
                        when (event) {
                            ProfileEvent.NavigateBack -> nestedNavigator.popBackStack()
                        }
                    },
                )
            }
        }
}
