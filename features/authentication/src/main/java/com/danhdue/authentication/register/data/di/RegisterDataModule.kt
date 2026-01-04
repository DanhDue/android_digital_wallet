/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.register.data.di

import com.danhdue.authentication.register.data.repository.DefaultRegisterRepository
import com.danhdue.authentication.register.domain.repository.RegisterRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides data layer dependencies for the Register feature.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RegisterDataModule {
    /**
     * Binds the repository implementation to its interface.
     */
    @Binds
    @Singleton
    abstract fun bindRegisterRepository(repository: DefaultRegisterRepository): RegisterRepository
}
