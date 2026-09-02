/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.androiddigitalwallet.split

import android.content.Context
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Provides the app-global [SplitInstallManager] (Task 14). `:app` is the only
 * module that knows about Play Feature Delivery; every other module depends on
 * the `:platform` `FeatureInstaller` interface.
 */
@Module
@InstallIn(SingletonComponent::class)
object SplitInstallProvidesModule {
    @Provides
    @Singleton
    fun provideSplitInstallManager(
        @ApplicationContext context: Context,
    ): SplitInstallManager = SplitInstallManagerFactory.create(context)
}
