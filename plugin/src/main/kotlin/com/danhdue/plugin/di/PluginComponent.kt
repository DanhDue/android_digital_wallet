/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.plugin.di

import com.danhdue.plugin.domain.repository.PluginRepository
import com.danhdue.plugin.domain.usecase.GetDataUseCase
import com.danhdue.plugin.domain.usecase.SyncDataUseCase
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [PluginModule::class])
interface PluginComponent {
    fun getPluginRepository(): PluginRepository
    fun getDataUseCase(): GetDataUseCase
    fun getSyncDataUseCase(): SyncDataUseCase
    fun getMyPluginViewModel(): com.danhdue.plugin.presentation.MyPluginViewModel

    @Component.Builder
    interface Builder {
        fun build(): PluginComponent
    }
}
