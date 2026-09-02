/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.settings.presentation

import app.cash.turbine.test
import com.danhdue.libraries.testutils.TestCoroutineRule
import com.danhdue.platform.AppEvent
import com.danhdue.platform.AppEventBus
import com.danhdue.settings.domain.model.Profile
import com.danhdue.settings.domain.model.Settings
import com.danhdue.settings.domain.repository.SettingsRepository
import com.danhdue.settings.domain.usecase.GetProfileDataUseCase
import com.danhdue.settings.domain.usecase.GetSettingsDataUseCase
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Task 11 pilot (publish side): the Settings ViewModel broadcasts
 * [AppEvent.ProfileNameChanged] on the [AppEventBus] with the profile display
 * name once the profile data loads; nothing is published when that load fails.
 */
class SettingsViewModelTest {
    @get:Rule
    val coroutineRule = TestCoroutineRule()

    private class FakeSettingsRepository(
        private val profileResult: Result<Profile>,
    ) : SettingsRepository {
        override suspend fun getSettingsData(): Result<Settings> = Result.success(Settings(id = "1", data = "ok"))

        override suspend fun getProfileData(): Result<Profile> = profileResult
    }

    private fun viewModel(
        bus: AppEventBus,
        profileResult: Result<Profile> = Result.success(Profile(id = "1", data = "Ada Lovelace")),
    ): SettingsViewModel {
        val repository = FakeSettingsRepository(profileResult)
        return SettingsViewModel(
            getSettingsDataUseCase = GetSettingsDataUseCase(repository),
            getProfileDataUseCase = GetProfileDataUseCase(repository),
            appEventBus = bus,
        )
    }

    @Test
    fun `publishes ProfileNameChanged with the loaded display name`() =
        coroutineRule.runTest {
            val bus = AppEventBus()

            bus.on<AppEvent.ProfileNameChanged>().test {
                viewModel(bus)

                assertEquals(AppEvent.ProfileNameChanged(displayName = "Ada Lovelace"), awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `does not publish ProfileNameChanged when the profile load fails`() =
        coroutineRule.runTest {
            val bus = AppEventBus()

            bus.on<AppEvent>().test {
                viewModel(bus, profileResult = Result.failure(IllegalStateException("offline")))

                expectNoEvents()
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `clears the loading flag once the initial load settles`() =
        coroutineRule.runTest {
            val viewModel = viewModel(AppEventBus())

            assertEquals(false, viewModel.state.value.isLoading)
        }
}
