/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.mywallet.presentation.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

/**
 * Hilt module that provides navigation entries for the Authentication feature.
 */
@Module
@InstallIn(ActivityRetainedComponent::class)
object MyWalletNavigationModule
