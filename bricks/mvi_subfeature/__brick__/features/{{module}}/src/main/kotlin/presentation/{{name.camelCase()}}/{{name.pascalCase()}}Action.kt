/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.{{module}}.presentation.{{name.camelCase()}}

/**
 * Defines the actions that can be sent from the UI to the ViewModel
 * for the {{name.pascalCase()}} feature.
 */
sealed interface {{name.pascalCase()}}Action {
    data object OnBackClicked : {{name.pascalCase()}}Action
}
