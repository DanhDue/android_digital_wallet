/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.{{module}}.presentation.{{name.camelCase()}}

/**
 * Defines the one-off events that the ViewModel can send to the UI.
 * These events are meant to be consumed only once (e.g., navigation, snackbar).
 */
sealed interface {{name.pascalCase()}}Event {
    data object NavigateBack : {{name.pascalCase()}}Event
}
