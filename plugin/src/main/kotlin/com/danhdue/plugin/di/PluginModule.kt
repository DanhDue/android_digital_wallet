/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.plugin.di

import android.content.Context
import com.danhdue.plugin.data.repository.PluginRepositoryImpl
import com.danhdue.plugin.domain.repository.PluginRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class PluginModule {

    @Binds
    @Singleton
    abstract fun bindPluginRepository(
        impl: PluginRepositoryImpl
    ): PluginRepository

    companion object {
        @Volatile
        private var appContext: Context? = null

        fun setContext(context: Context) {
            appContext = context.applicationContext ?: context
        }

        @Provides
        @Singleton
        fun provideContext(): Context {
            return appContext ?: throw IllegalStateException("Context not initialized in PluginModule")
        }
    }
}
