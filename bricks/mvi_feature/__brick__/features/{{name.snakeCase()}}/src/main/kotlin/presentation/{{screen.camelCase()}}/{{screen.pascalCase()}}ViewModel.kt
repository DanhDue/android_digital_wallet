/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package {{package}}.presentation.{{screen.camelCase()}}

import com.danhdue.framework.base.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import {{package}}.domain.usecase.Get{{name.pascalCase()}}DataUseCase
import javax.inject.Inject

/**
 * Manages the business logic and state for the {{screen.pascalCase()}} feature.
 */
@HiltViewModel
class {{screen.pascalCase()}}ViewModel
    @Inject
    constructor(
        private val get{{name.pascalCase()}}DataUseCase: Get{{name.pascalCase()}}DataUseCase,
    ) : MviViewModel<{{screen.pascalCase()}}State, {{screen.pascalCase()}}Action, {{screen.pascalCase()}}Event>(
        initialState = {{screen.pascalCase()}}State(),
    ) {
        init {
            Timber.d("{{screen.pascalCase()}}ViewModel init")
        }

        override fun onAction(action: {{screen.pascalCase()}}Action) {
            when (action) {
                {{screen.pascalCase()}}Action.OnBackClicked -> {
                    sendEvent({{screen.pascalCase()}}Event.NavigateBack)
                }
            }
        }
    }
