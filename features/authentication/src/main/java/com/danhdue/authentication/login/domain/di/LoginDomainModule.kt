/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.login.domain.di

import com.danhdue.authentication.login.domain.repository.LoginRepository
import com.danhdue.authentication.login.domain.usecase.GetLoginDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Hilt module that provides domain layer dependencies (use cases) for the Login feature.
 */
@Module
@InstallIn(ViewModelComponent::class)
object LoginDomainModule {
    /**
     * Provides the GetLoginDataUseCase instance.
     */
    @Provides
    @ViewModelScoped
    fun provideGetLoginDataUseCase(repository: LoginRepository): GetLoginDataUseCase = GetLoginDataUseCase(repository)
}
