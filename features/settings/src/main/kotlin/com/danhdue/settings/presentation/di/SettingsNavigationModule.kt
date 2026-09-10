/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.presentation.di

import com.danhdue.framework.navigation.LocalNestedNavigator
import com.danhdue.platform.AppRoutes
import com.danhdue.platform.EntryProviderInstaller
import com.danhdue.platform.deeplink.DeepLinkResolver
import com.danhdue.settings.presentation.SettingsEvent
import com.danhdue.settings.presentation.SettingsRoot
import com.danhdue.settings.presentation.profile.ProfileEvent
import com.danhdue.settings.presentation.profile.ProfileRoot
import com.danhdue.settings.presentation.profile.ProfileRoute
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
object SettingsNavigationModule {
    @Provides
    @IntoSet
    fun provideSettingsDeepLinkResolver(resolver: SettingsDeepLinkResolver): DeepLinkResolver = resolver

    @Provides
    @IntoSet
    fun provideSettingsEntries(): EntryProviderInstaller =
        {
            entry<AppRoutes.SettingsRoute> {
                val nestedNavigator = LocalNestedNavigator.current
                SettingsRoot(
                    onEvent = { event ->
                        when (event) {
                            SettingsEvent.NavigateToProfile -> nestedNavigator.navigate(ProfileRoute)
                            SettingsEvent.NavigateToSecurity -> Unit
                            SettingsEvent.NavigateToDeveloperOptions -> Unit
                            is SettingsEvent.ShowToast -> Unit
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
