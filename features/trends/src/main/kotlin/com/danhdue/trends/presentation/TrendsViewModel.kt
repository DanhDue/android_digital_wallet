/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.trends.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danhdue.trends.domain.usecase.GetTrendsDataUseCase
import com.danhdue.trends.presentation.model.TrendsUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Manages the business logic and state for the Trends feature.
 */
@HiltViewModel
class TrendsViewModel
    @Inject
    constructor(
        private val getTrendsDataUseCase: GetTrendsDataUseCase,
    ) : ViewModel() {
        private val _state = MutableStateFlow(TrendsState())
        val state = _state.asStateFlow()

        private val _event = MutableSharedFlow<TrendsEvent>()
        val event = _event.asSharedFlow()

        init {
            loadInitialData()
        }

        fun onAction(action: TrendsAction) {
            when (action) {
                is TrendsAction.SearchQueryChanged -> {
                    _state.update { it.copy(searchQuery = action.query) }
                }
                is TrendsAction.CoinClicked -> {
                    // Handle coin click (e.g., navigate to detail)
                }
            }
        }

        private fun loadInitialData() {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }

                // For now, using mock data as requested by the UI design
                val mockCoins = listOf(
                    TrendsUiModel("1", "BTC", "Bitcoin", "https://cryptologos.cc/logos/bitcoin-btc-logo.png", "$90,921.15", "$39.33B", -1.81, "-1.81%"),
                    TrendsUiModel("2", "ETH", "Ethereum", "https://cryptologos.cc/logos/ethereum-eth-logo.png", "$3,150.55", "$22.07B", -3.20, "-3.20%"),
                    TrendsUiModel("3", "USDT", "Tether", "https://cryptologos.cc/logos/tether-usdt-logo.png", "$1.00", "$84.51B", -0.01, "-0.01%"),
                    TrendsUiModel("4", "XRP", "XRP", "https://cryptologos.cc/logos/xrp-xrp-logo.png", "$2.16", "$3.99B", -4.40, "-4.40%"),
                    TrendsUiModel("5", "BNB", "BNB", "https://cryptologos.cc/logos/bnb-bnb-logo.png", "$893.86", "$2.31B", -1.79, "-1.79%"),
                    TrendsUiModel("6", "SOL", "Solana", "https://cryptologos.cc/logos/solana-sol-logo.png", "$137.59", "$3.91B", -0.91, "-0.91%"),
                    TrendsUiModel("7", "USDC", "USDC", "https://cryptologos.cc/logos/usd-coin-usdc-logo.png", "$1.00", "$11.45B", 0.05, "+0.05%"),
                    TrendsUiModel("8", "TRX", "TRON", "https://cryptologos.cc/logos/tron-trx-logo.png", "$0.30", "$648.47M", 1.13, "+1.13%"),
                    TrendsUiModel("9", "DOGE", "Dogecoin", "https://cryptologos.cc/logos/dogecoin-doge-logo.png", "$0.15", "$1.49B", -0.85, "-0.85%"),
                    TrendsUiModel("10", "ADA", "Cardano", "https://cryptologos.cc/logos/cardano-ada-logo.png", "$0.40", "$612.11M", -2.79, "-2.79%"),
                    TrendsUiModel("11", "BCH", "Bitcoin Cash", "https://cryptologos.cc/logos/bitcoin-cash-bch-logo.png", "$640.84", "$317.87M", 1.36, "+1.36%"),
                )

                _state.update { it.copy(items = mockCoins, isLoading = false) }

                // Real data loading could happen here
                /*
                getTrendsDataUseCase()
                    .onSuccess {
                    }.onFailure {
                    }
                */
            }
        }
    }
