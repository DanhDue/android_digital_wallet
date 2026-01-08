/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.mywallet.presentation.mynfts

import com.danhdue.framework.base.mvvm.MvvmViewModel
import com.danhdue.framework.network.NetworkResult
import com.danhdue.mywallet.domain.usecase.GetMyNFTsDataUseCase
import com.danhdue.mywallet.presentation.model.MyNFTsUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Manages the business logic and state for the MyNFTs feature.
 */
@HiltViewModel
class MyNFTsViewModel @Inject constructor(
    private val getMyNFTsDataUseCase: GetMyNFTsDataUseCase,
) : MvvmViewModel() {
    private val _state = MutableStateFlow(MyNFTsState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<MyNFTsEvent>()
    val event = _event.asSharedFlow()

    init {
        loadInitialData()
    }

    fun onAction(action: MyNFTsAction) {
        when (action) {
            MyNFTsAction.Refresh -> loadInitialData()
        }
    }

    @Suppress("UnusedPrivateProperty")
    private fun loadInitialData() {
        safeLaunch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = getMyNFTsDataUseCase()) {
                is NetworkResult.Success -> {
                    // In a real app, we'd map domain model to UI model
                    val mockNFTs = listOf(
                        MyNFTsUiModel(
                            id = "1",
                            name = "Ape #1",
                            collectionName = "Bored Ape Yacht Club",
                            imageUrl = ""
                        ),
                        MyNFTsUiModel(
                            id = "2",
                            name = "Punk #2",
                            collectionName = "CryptoPunks",
                            imageUrl = ""
                        )
                    )
                    _state.value = _state.value.copy(items = mockNFTs)
                }
                is NetworkResult.Error -> {
                    _event.emit(MyNFTsEvent.ShowSnackbar("Failed to load NFTs"))
                }
            }

            _state.value = _state.value.copy(isLoading = false)
        }
    }
}
