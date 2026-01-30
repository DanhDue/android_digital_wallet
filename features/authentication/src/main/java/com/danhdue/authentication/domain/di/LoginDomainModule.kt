/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.domain.di

import com.danhdue.authentication.domain.repository.LoginRepository
import com.danhdue.authentication.domain.usecase.LoginUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/** Hilt module that provides domain layer dependencies (use cases) for the Login feature. */
@Module
@InstallIn(ViewModelComponent::class)
object LoginDomainModule {
    /** Provides the LoginUseCase instance. */
    @Provides
    @ViewModelScoped
    fun provideLoginUseCase(repository: LoginRepository): LoginUseCase = LoginUseCase(repository)
}
