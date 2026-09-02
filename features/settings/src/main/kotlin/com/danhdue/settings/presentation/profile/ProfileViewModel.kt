/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.presentation.profile

import com.danhdue.framework.base.mvi.MviViewModel
import com.danhdue.settings.domain.usecase.GetProfileDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Manages the business logic and state for the Profile feature.
 *
 * MVI: extends [MviViewModel]; UI intents arrive through [onAction], state is
 * mutated with [reduce], one-off navigation events go out via `sendEvent`.
 */
@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(
        private val getProfileDataUseCase: GetProfileDataUseCase,
    ) : MviViewModel<ProfileState, ProfileAction, ProfileEvent>(
            initialState = ProfileState(),
        ) {
        init {
            loadInitialData()
        }

        override fun onAction(action: ProfileAction) {
            when (action) {
                ProfileAction.OnBackClicked -> sendEvent(ProfileEvent.NavigateBack)
            }
        }

        private fun loadInitialData() {
            safeLaunch {
                reduce { copy(isLoading = true) }

                getProfileDataUseCase()
                    .onSuccess { }
                    .onFailure { }

                reduce { copy(isLoading = false) }
            }
        }
    }
