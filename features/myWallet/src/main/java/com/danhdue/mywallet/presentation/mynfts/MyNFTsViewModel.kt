/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.mywallet.presentation.mynfts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danhdue.mywallet.domain.usecase.GetMyNFTsDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Manages the business logic and state for the MyNFTs feature.
 */
@HiltViewModel
class MyNFTsViewModel @Inject constructor(
    private val getMyNFTsDataUseCase: GetMyNFTsDataUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(MyNFTsState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<MyNFTsEvent>()
    val event = _event.asSharedFlow()

    init {
        loadInitialData()
    }

    fun onAction(action: MyNFTsAction) {
        when (action) {
            else -> {
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            getMyNFTsDataUseCase()
                .onSuccess {
                }.onFailure {
                }

            _state.update { it.copy(isLoading = false) }
        }
    }
}
