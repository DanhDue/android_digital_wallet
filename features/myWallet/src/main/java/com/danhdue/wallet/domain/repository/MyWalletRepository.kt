/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.wallet.domain.repository

import com.danhdue.core.network.DataState
import com.danhdue.wallet.domain.model.MyNFTs
import com.danhdue.wallet.domain.model.MyTokens
import com.danhdue.wallet.domain.model.MyWallet

/**
 * Interface defining the contract for the MyWallet feature's repository.
 */
interface MyWalletRepository {
    /**
     * Retrieves data for the MyWallet feature.
     */
    suspend fun getMyWalletData(): DataState<MyWallet>

    /**
     * Retrieves data for the MyNFTs feature.
     */
    suspend fun getMyNFTsData(): DataState<MyNFTs>

    /**
     * Retrieves data for the MyTokens feature.
     */
    suspend fun getMyTokensData(): DataState<MyTokens>
}
