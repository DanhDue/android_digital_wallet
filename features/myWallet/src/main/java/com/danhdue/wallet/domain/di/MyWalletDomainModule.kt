/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.wallet.domain.di

import com.danhdue.wallet.domain.repository.MyWalletRepository
import com.danhdue.wallet.domain.usecase.GetMyNFTsDataUseCase
import com.danhdue.wallet.domain.usecase.GetMyTokensDataUseCase
import com.danhdue.wallet.domain.usecase.GetMyWalletDataUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Hilt module that provides domain layer dependencies (use cases) for the MyWallet feature.
 */
@Module
@InstallIn(ViewModelComponent::class)
object MyWalletDomainModule {
    /**
     * Provides the GetMyWalletDataUseCase instance.
     */
    @Provides
    @ViewModelScoped
    fun provideGetMyWalletDataUseCase(repository: MyWalletRepository): GetMyWalletDataUseCase = GetMyWalletDataUseCase(repository)

    /**
     * Provides the GetMyNFTsDataUseCase instance.
     */
    @Provides
    @ViewModelScoped
    fun provideGetMyNFTsDataUseCase(repository: MyWalletRepository): GetMyNFTsDataUseCase = GetMyNFTsDataUseCase(repository)

    /**
     * Provides the GetMyTokensDataUseCase instance.
     */
    @Provides
    @ViewModelScoped
    fun provideGetMyTokensDataUseCase(repository: MyWalletRepository): GetMyTokensDataUseCase = GetMyTokensDataUseCase(repository)
}
