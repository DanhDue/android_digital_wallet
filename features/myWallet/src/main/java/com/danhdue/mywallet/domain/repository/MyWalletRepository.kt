/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.mywallet.domain.repository

import com.danhdue.framework.network.NetworkResult
import com.danhdue.mywallet.domain.model.MyNFTs
import com.danhdue.mywallet.domain.model.MyTokens
import com.danhdue.mywallet.domain.model.MyWallet

/**
 * Interface defining the contract for the MyWallet feature's repository.
 */
interface MyWalletRepository {
    /**
     * Retrieves data for the MyWallet feature.
     */
    suspend fun getMyWalletData(): NetworkResult<MyWallet>

    /**
     * Retrieves data for the MyNFTs feature.
     */
    suspend fun getMyNFTsData(): NetworkResult<MyNFTs>

    /**
     * Retrieves data for the MyTokens feature.
     */
    suspend fun getMyTokensData(): NetworkResult<MyTokens>
}
