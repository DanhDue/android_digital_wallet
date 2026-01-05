/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.data.di

import com.danhdue.authentication.data.repository.DefaultLoginRepository
import com.danhdue.authentication.domain.repository.LoginRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides data layer dependencies for the Login feature.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class LoginDataModule {
    /**
     * Binds the repository implementation to its interface.
     */
    @Binds
    @Singleton
    abstract fun bindLoginRepository(repository: DefaultLoginRepository): LoginRepository
}
