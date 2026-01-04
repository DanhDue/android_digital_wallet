/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.register.domain.di

import com.danhdue.authentication.register.domain.repository.RegisterRepository
import com.danhdue.authentication.register.domain.usecase.GetRegisterDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Hilt module that provides domain layer dependencies (use cases) for the Register feature.
 */
@Module
@InstallIn(ViewModelComponent::class)
object RegisterDomainModule {
    /**
     * Provides the GetRegisterDataUseCase instance.
     */
    @Provides
    @ViewModelScoped
    fun provideGetRegisterDataUseCase(repository: RegisterRepository): GetRegisterDataUseCase = GetRegisterDataUseCase(repository)
}
