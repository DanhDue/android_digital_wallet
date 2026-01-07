/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.mywallet.domain.usecase

import com.danhdue.mywallet.domain.model.MyTokens
import com.danhdue.mywallet.presentation.mytokens.domain.repository.MyTokensRepository
import javax.inject.Inject

/**
 * Use case that encapsulates the business logic for fetching the MyTokens feature data.
 */
class GetMyTokensDataUseCase @Inject constructor(
    private val repository: MyTokensRepository,
) {
    /**
     * Executes the use case.
     */
    suspend operator fun invoke(): Result<MyTokens> = repository.getMyTokensData()
}
