/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.wallet.presentation.mytokens

import com.danhdue.framework.base.mvvm.MvvmViewModel
import com.danhdue.framework.network.NetworkResult
import com.danhdue.wallet.domain.usecase.GetMyTokensDataUseCase
import com.danhdue.wallet.presentation.model.MyTokensUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Manages the business logic and state for the MyTokens feature.
 */
@HiltViewModel
class MyTokensViewModel @Inject constructor(
    private val getMyTokensDataUseCase: GetMyTokensDataUseCase,
) : MvvmViewModel() {
    private val _state = MutableStateFlow(MyTokensState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<MyTokensEvent>()
    val event = _event.asSharedFlow()

    init {
        loadInitialData()
    }

    fun onAction(action: MyTokensAction) {
        when (action) {
            MyTokensAction.Refresh -> loadInitialData()
        }
    }

    @Suppress("MagicNumber", "UnusedPrivateProperty")
    private fun loadInitialData() {
        safeLaunch {
            _state.value = _state.value.copy(isLoading = true)

            when (val result = getMyTokensDataUseCase()) {
                is NetworkResult.Success -> {
                    val mockTokens =
                        listOf(
                            MyTokensUiModel(
                                id = "1",
                                symbol = "ETH",
                                name = "Ethereum",
                                iconUrl = "",
                                balance = "1.3135 ETH",
                                fiatBalance = "$2,430.34",
                                price = "$1,850.45",
                                priceChange = "+4.86%",
                                isPositive = true,
                                sparklineData = listOf(0.1f, 0.3f, 0.2f, 0.5f, 0.4f, 0.7f, 0.6f, 0.9f),
                            ),
                        )
                    _state.value = _state.value.copy(items = mockTokens)
                }
                is NetworkResult.Error -> {
                    _event.emit(MyTokensEvent.ShowSnackbar("Failed to load tokens"))
                }
            }

            _state.value = _state.value.copy(isLoading = false)
        }
    }
}
