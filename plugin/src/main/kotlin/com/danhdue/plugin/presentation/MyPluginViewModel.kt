/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.plugin.presentation

import androidx.lifecycle.viewModelScope
import com.danhdue.plugin.domain.usecase.GetDataUseCase
import com.danhdue.plugin.domain.usecase.SyncDataUseCase
import com.danhdue.plugin.presentation.base.MviViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyPluginViewModel @Inject constructor(
    private val getDataUseCase: GetDataUseCase,
    private val syncDataUseCase: SyncDataUseCase
) : MviViewModel<MyPluginAction, MyPluginState, MyPluginEvent>(MyPluginState()) {

    private var loadDataJob: Job? = null

    override fun onAction(action: MyPluginAction) {
        when (action) {
            is MyPluginAction.LoadData, is MyPluginAction.Refresh -> loadData()
            is MyPluginAction.Sync -> syncData()
        }
    }

    private fun loadData() {
        loadDataJob?.cancel()
        loadDataJob = viewModelScope.launch {
            setState { copy(isLoading = true, errorMessage = null) }
            val outcome = getDataUseCase.execute()
            if (outcome.isSuccess) {
                setState { copy(isLoading = false, data = outcome.getOrNull(), errorMessage = null) }
            } else {
                setState { copy(isLoading = false, errorMessage = outcome.exceptionOrNull()?.message ?: "Unknown error") }
            }
        }
    }

    private fun syncData() {
        viewModelScope.launch {
            val result = syncDataUseCase.execute()
            if (result.isSuccess && result.getOrNull() == true) {
                sendEvent(MyPluginEvent.ShowToast("Data synchronized successfully"))
            } else {
                sendEvent(MyPluginEvent.ShowToast("Data sync failed"))
            }
        }
    }
}
