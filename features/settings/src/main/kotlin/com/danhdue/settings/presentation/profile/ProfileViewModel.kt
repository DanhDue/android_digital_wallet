/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danhdue.settings.domain.usecase.GetProfileDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Manages the business logic and state for the Profile feature.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileDataUseCase: GetProfileDataUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<ProfileEvent>()
    val event = _event.asSharedFlow()

    init {
        loadInitialData()
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.OnBackClicked -> {
                viewModelScope.launch {
                    _event.emit(ProfileEvent.NavigateBack)
                }
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            getProfileDataUseCase()
                .onSuccess {
                }.onFailure {
                }

            _state.update { it.copy(isLoading = false) }
        }
    }
}
