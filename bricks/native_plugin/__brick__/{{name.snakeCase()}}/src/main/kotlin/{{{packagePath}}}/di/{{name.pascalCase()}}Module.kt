/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package {{package}}.di

import android.content.Context
import {{package}}.data.repository.{{name.pascalCase()}}RepositoryImpl
import {{package}}.domain.repository.{{name.pascalCase()}}Repository
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class {{name.pascalCase()}}Module {

    @Binds
    @Singleton
    abstract fun bind{{name.pascalCase()}}Repository(
        impl: {{name.pascalCase()}}RepositoryImpl
    ): {{name.pascalCase()}}Repository

    companion object {
        @Volatile
        private var appContext: Context? = null

        fun setContext(context: Context) {
            appContext = context.applicationContext ?: context
        }

        @Provides
        @Singleton
        fun provideContext(): Context {
            return appContext ?: throw IllegalStateException("Context not initialized in {{name.pascalCase()}}Module")
        }
    }
}
