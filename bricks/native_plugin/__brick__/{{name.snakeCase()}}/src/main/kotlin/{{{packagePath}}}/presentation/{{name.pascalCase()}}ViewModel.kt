/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package {{package}}.presentation

import androidx.lifecycle.viewModelScope
import {{package}}.domain.usecase.Get{{name.pascalCase()}}DataUseCase
import {{package}}.domain.usecase.Sync{{name.pascalCase()}}DataUseCase
import {{package}}.presentation.base.MviViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class {{name.pascalCase()}}ViewModel @Inject constructor(
    private val getDataUseCase: Get{{name.pascalCase()}}DataUseCase,
    private val syncDataUseCase: Sync{{name.pascalCase()}}DataUseCase
) : MviViewModel<{{name.pascalCase()}}Action, {{name.pascalCase()}}State, {{name.pascalCase()}}Event>({{name.pascalCase()}}State()) {

    private var loadDataJob: Job? = null

    override fun onAction(action: {{name.pascalCase()}}Action) {
        when (action) {
            is {{name.pascalCase()}}Action.LoadData, is {{name.pascalCase()}}Action.Refresh -> loadData()
            is {{name.pascalCase()}}Action.Sync -> syncData()
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
                sendEvent({{name.pascalCase()}}Event.ShowToast("Data synchronized successfully"))
            } else {
                sendEvent({{name.pascalCase()}}Event.ShowToast("Data sync failed"))
            }
        }
    }
}
