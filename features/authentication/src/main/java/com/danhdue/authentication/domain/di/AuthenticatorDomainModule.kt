/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.authentication.domain.di

import com.danhdue.authentication.domain.repository.AuthenticatorRepository
import com.danhdue.authentication.domain.usecase.GetAuthenticatorDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Hilt module that provides domain layer dependencies (use cases) for the Authenticator feature.
 */
@Module
@InstallIn(ViewModelComponent::class)
object AuthenticatorDomainModule {

    /**
     * Provides the GetAuthenticatorDataUseCase instance.
     */
    @Provides
    @ViewModelScoped
    fun provideGetAuthenticatorDataUseCase(
        repository: AuthenticatorRepository
    ): GetAuthenticatorDataUseCase {
        return GetAuthenticatorDataUseCase(repository)
    }
}