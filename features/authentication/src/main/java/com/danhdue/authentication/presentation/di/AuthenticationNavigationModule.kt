/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.presentation.di

import com.danhdue.authentication.presentation.login.LoginEvent
import com.danhdue.authentication.presentation.login.LoginRoot
import com.danhdue.authentication.presentation.login.LoginRoute
import com.danhdue.authentication.presentation.register.RegisterEvent
import com.danhdue.authentication.presentation.register.RegisterRoot
import com.danhdue.authentication.presentation.register.RegisterRoute
import com.danhdue.framework.navigation.EntryProviderInstaller
import com.danhdue.framework.navigation.Navigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet

/**
 * Hilt module that provides navigation entries for the Authentication feature.
 */
@Module
@InstallIn(ActivityRetainedComponent::class)
object AuthenticationNavigationModule {
    @Provides
    @IntoSet
    fun provideAuthenticationEntries(navigator: Navigator): EntryProviderInstaller =
        {
            entry<LoginRoute> {
                LoginRoot(
                    onEvent = { event ->
                        when (event) {
                            LoginEvent.NavigateToRegister -> navigator.navigateTo(RegisterRoute)
                            LoginEvent.NavigateBack -> navigator.popBackStack()
                            else -> {}
                        }
                    },
                )
            }
            entry<RegisterRoute> {
                RegisterRoot(
                    onEvent = { event ->
                        when (event) {
                            RegisterEvent.NavigateToLogin -> navigator.navigateTo(LoginRoute)
                            RegisterEvent.NavigateBack -> navigator.popBackStack()
                        }
                    },
                )
            }
        }
}
