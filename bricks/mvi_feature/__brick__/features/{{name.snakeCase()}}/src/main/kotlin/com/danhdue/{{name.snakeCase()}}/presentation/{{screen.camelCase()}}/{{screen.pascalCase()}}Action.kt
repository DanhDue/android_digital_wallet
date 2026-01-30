/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package {{package}}.presentation.{{screen.camelCase()}}

/**
 * Defines the actions that can be sent from the UI to the ViewModel
 * for the {{screen.pascalCase()}} feature.
 */
sealed interface {{screen.pascalCase()}}Action {
    data object OnBackClicked : {{screen.pascalCase()}}Action
}
