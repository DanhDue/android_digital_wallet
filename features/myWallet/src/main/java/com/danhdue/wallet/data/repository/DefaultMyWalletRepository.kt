/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.wallet.data.repository

import com.danhdue.framework.network.DataState
import com.danhdue.wallet.domain.model.MyNFTs
import com.danhdue.wallet.domain.model.MyTokens
import com.danhdue.wallet.domain.model.MyWallet
import com.danhdue.wallet.domain.repository.MyWalletRepository
import javax.inject.Inject

/**
 * Concrete implementation of the repository for the MyWallet feature.
 */
@Suppress("StringLiteralDuplication")
class DefaultMyWalletRepository @Inject constructor() : MyWalletRepository {
    override suspend fun getMyWalletData(): DataState<MyWallet> =
        try {
            val domainModel = MyWallet(id = "1", data = "Sample data from repository")
            DataState.Success(domainModel)
        } catch (e: Exception) {
            DataState.Error(e)
        }

    override suspend fun getMyNFTsData(): DataState<MyNFTs> =
        try {
            val domainModel = MyNFTs(id = "1", data = "Sample data from repository")
            DataState.Success(domainModel)
        } catch (e: Exception) {
            DataState.Error(e)
        }

    override suspend fun getMyTokensData(): DataState<MyTokens> =
        try {
            val domainModel = MyTokens(id = "1", data = "Sample data from repository")
            DataState.Success(domainModel)
        } catch (e: Exception) {
            DataState.Error(e)
        }
}
