/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.presentation

import com.danhdue.framework.base.mvi.MviViewModel
import com.danhdue.platform.AppEvent
import com.danhdue.platform.AppEventBus
import com.danhdue.settings.domain.usecase.GetProfileDataUseCase
import com.danhdue.settings.domain.usecase.GetSettingsDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Manages the business logic and state for the Settings feature.
 *
 * MVI: extends [MviViewModel]; UI intents arrive through [onAction], state is
 * mutated with [reduce], one-off navigation events go out via `sendEvent`.
 *
 * Task 11 pilot: once the profile data loads, the ViewModel broadcasts
 * [AppEvent.ProfileNameChanged] on the [AppEventBus] so a host (`:shell`) can
 * render the name in shell-owned chrome without importing anything from
 * `com.danhdue.settings.*`. This closes the Android analogue of the Flutter
 * shell's "notify tab" gap.
 */
@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        private val getSettingsDataUseCase: GetSettingsDataUseCase,
        private val getProfileDataUseCase: GetProfileDataUseCase,
        private val appEventBus: AppEventBus,
    ) : MviViewModel<SettingsState, SettingsAction, SettingsEvent>(
            initialState = SettingsState(),
        ) {
        init {
            loadInitialData()
        }

        override fun onAction(action: SettingsAction) {
            when (action) {
                is SettingsAction.OpenProfile -> sendEvent(SettingsEvent.NavigateToProfile)
            }
        }

        private fun loadInitialData() {
            safeLaunch {
                reduce { copy(isLoading = true) }

                getSettingsDataUseCase()
                    .onSuccess { }
                    .onFailure { }

                getProfileDataUseCase()
                    .onSuccess { profile ->
                        appEventBus.publish(AppEvent.ProfileNameChanged(displayName = profile.data))
                    }.onFailure { }

                reduce { copy(isLoading = false) }
            }
        }
    }
