/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.{{module}}.presentation.{{name.camelCase()}}

import com.danhdue.framework.base.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

/**
 * Manages the business logic and state for the {{name.pascalCase()}} feature.
 */
@HiltViewModel
class {{name.pascalCase()}}ViewModel
    @Inject
    constructor() : MviViewModel<{{name.pascalCase()}}State, {{name.pascalCase()}}Action, {{name.pascalCase()}}Event>(
        initialState = {{name.pascalCase()}}State(),
    ) {
        init {
            Timber.d("{{name.pascalCase()}}ViewModel init")
        }

        override fun onAction(action: {{name.pascalCase()}}Action) {
            when (action) {
                {{name.pascalCase()}}Action.OnBackClicked -> {
                    sendEvent({{name.pascalCase()}}Event.NavigateBack)
                }
            }
        }
    }
