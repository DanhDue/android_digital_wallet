/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.mywallet.data.repository

import com.danhdue.framework.network.NetworkResult
import com.danhdue.mywallet.domain.model.MyNFTs
import com.danhdue.mywallet.domain.model.MyTokens
import com.danhdue.mywallet.domain.model.MyWallet
import com.danhdue.mywallet.domain.repository.MyWalletRepository
import javax.inject.Inject

/**
 * Concrete implementation of the repository for the MyWallet feature.
 */
@Suppress("StringLiteralDuplication")
class DefaultMyWalletRepository @Inject constructor() : MyWalletRepository {
    override suspend fun getMyWalletData(): NetworkResult<MyWallet> =
        try {
            val domainModel = MyWallet(id = "1", data = "Sample data from repository")
            NetworkResult.Success(domainModel)
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }

    override suspend fun getMyNFTsData(): NetworkResult<MyNFTs> =
        try {
            val domainModel = MyNFTs(id = "1", data = "Sample data from repository")
            NetworkResult.Success(domainModel)
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }

    override suspend fun getMyTokensData(): NetworkResult<MyTokens> =
        try {
            val domainModel = MyTokens(id = "1", data = "Sample data from repository")
            NetworkResult.Success(domainModel)
        } catch (e: Exception) {
            NetworkResult.Error(e)
        }
}
