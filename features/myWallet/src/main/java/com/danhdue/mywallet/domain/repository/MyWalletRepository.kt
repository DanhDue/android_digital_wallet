/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.mywallet.domain.repository

import com.danhdue.mywallet.domain.model.MyNFTs
import com.danhdue.mywallet.domain.model.MyTokens
import com.danhdue.mywallet.domain.model.MyWallet

/**
 * Interface defining the contract for the MyWallet feature's repository.
 */
interface MyWalletRepository {
    /**
     * Retrieves data for the MyWallet feature.
     *
     * @return A Result object containing the MyWallet domain model on success,
     * or an exception on failure.
     */
    suspend fun getMyWalletData(): Result<MyWallet>

    /**
     * Retrieves data for the MyNFTs feature.
     *
     * @return A Result object containing the MyNFTs domain model on success,
     * or an exception on failure.
     */
    suspend fun getMyNFTsData(): Result<MyNFTs>

    /**
     * Retrieves data for the MyTokens feature.
     *
     * @return A Result object containing the MyTokens domain model on success,
     * or an exception on failure.
     */
    suspend fun getMyTokensData(): Result<MyTokens>
}
