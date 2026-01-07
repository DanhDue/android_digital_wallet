/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.mywallet.domain.usecase

import com.danhdue.mywallet.domain.model.MyNFTs
import com.danhdue.mywallet.presentation.mynfts.domain.repository.MyNFTsRepository
import javax.inject.Inject

/**
 * Use case that encapsulates the business logic for fetching the MyNFTs feature data.
 */
class GetMyNFTsDataUseCase @Inject constructor(
    private val repository: MyNFTsRepository,
) {
    /**
     * Executes the use case.
     */
    suspend operator fun invoke(): Result<MyNFTs> = repository.getMyNFTsData()
}
