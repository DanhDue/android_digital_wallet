/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.androiddigitalwallet.split

import com.danhdue.platform.FeatureInstaller
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds the production [FeatureInstaller] to [FeatureInstallerImpl] (Task 14), so
 * release/debug builds use the real `SplitInstallManager` bridge. Unit tests keep
 * [com.danhdue.platform.NoOpFeatureInstaller] (treats every module as present).
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SplitInstallBindsModule {
    @Binds
    @Singleton
    abstract fun bindFeatureInstaller(impl: FeatureInstallerImpl): FeatureInstaller
}
