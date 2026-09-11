/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package {{package}}.presentation

import {{package}}.domain.model.{{name.pascalCase()}}Data
import {{package}}.presentation.base.BaseAction
import {{package}}.presentation.base.BaseEvent
import {{package}}.presentation.base.BaseState

sealed interface {{name.pascalCase()}}Action : BaseAction {
    object LoadData : {{name.pascalCase()}}Action
    object Refresh : {{name.pascalCase()}}Action
    object Sync : {{name.pascalCase()}}Action
}

data class {{name.pascalCase()}}State(
    val isLoading: Boolean = false,
    val data: {{name.pascalCase()}}Data? = null,
    val errorMessage: String? = null
) : BaseState

sealed interface {{name.pascalCase()}}Event : BaseEvent {
    data class ShowToast(val message: String) : {{name.pascalCase()}}Event
}
