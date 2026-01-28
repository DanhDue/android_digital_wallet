/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.{{module}}.data.di

import com.danhdue.{{module}}.data.repository.{{name.pascalCase()}}RepositoryImpl
import com.danhdue.{{module}}.domain.repository.{{name.pascalCase()}}Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides data layer dependencies for the {{name.pascalCase()}} subfeature.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class {{name.pascalCase()}}DataModule {
    @Binds
    @Singleton
    abstract fun bind{{name.pascalCase()}}Repository(
        impl: {{name.pascalCase()}}RepositoryImpl,
    ): {{name.pascalCase()}}Repository
}
