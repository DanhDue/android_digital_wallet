/*
 * Copyright © 2024, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.androiddigitalwallet.di

import com.danhdue.authentication.presentation.login.LoginRoute
import com.danhdue.framework.navigation.Navigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ExecutorNetworkIO

@Module
@InstallIn(ActivityRetainedComponent::class)
class AppNavigationModule {
    @Provides
    @ActivityRetainedScoped
    fun provideNavigator(): Navigator = Navigator(startDestination = LoginRoute)
}
