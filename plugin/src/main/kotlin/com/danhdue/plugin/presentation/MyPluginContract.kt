/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.plugin.presentation

import com.danhdue.plugin.domain.model.PluginData
import com.danhdue.plugin.presentation.base.BaseAction
import com.danhdue.plugin.presentation.base.BaseEvent
import com.danhdue.plugin.presentation.base.BaseState

sealed interface MyPluginAction : BaseAction {
    object LoadData : MyPluginAction
    object Refresh : MyPluginAction
    object Sync : MyPluginAction
}

data class MyPluginState(
    val isLoading: Boolean = false,
    val data: PluginData? = null,
    val errorMessage: String? = null
) : BaseState

sealed interface MyPluginEvent : BaseEvent {
    data class ShowToast(val message: String) : MyPluginEvent
}
