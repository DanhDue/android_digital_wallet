/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.wallet.domain.usecase

import com.danhdue.framework.network.NetworkResult
import com.danhdue.wallet.domain.model.MyTokens
import com.danhdue.wallet.domain.repository.MyWalletRepository
import javax.inject.Inject

/**
 * Use case that encapsulates the business logic for fetching the MyTokens feature data.
 */
class GetMyTokensDataUseCase @Inject constructor(
    private val repository: MyWalletRepository,
) {
    /**
     * Executes the use case.
     */
    suspend operator fun invoke(): NetworkResult<MyTokens> = repository.getMyTokensData()
}
