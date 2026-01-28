/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.{{module}}.domain.di

import com.danhdue.{{module}}.domain.repository.{{name.pascalCase()}}Repository
import com.danhdue.{{module}}.domain.usecase.Get{{name.pascalCase()}}DataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Hilt module that provides domain layer dependencies for the {{name.pascalCase()}} subfeature.
 */
@Module
@InstallIn(ViewModelComponent::class)
object {{name.pascalCase()}}DomainModule {
    /**
     * Provides the Get{{name.pascalCase()}}DataUseCase instance.
     */
    @Provides
    @ViewModelScoped
    fun provideGet{{name.pascalCase()}}DataUseCase(repository: {{name.pascalCase()}}Repository): Get{{name.pascalCase()}}DataUseCase =
        Get{{name.pascalCase()}}DataUseCase(repository)
}
