package com.rumble.battles.landing

import kotlinx.coroutines.flow.StateFlow

interface AppUpdateHandler {
    val appUpdateState: StateFlow<AppUpdateState>

    fun onSuggestedUpdateDismissed()
    fun onGoToStore()
}

data class AppUpdateState(
    val forceUpdateRequired: Boolean = false,
    val appUpdateSuggested: Boolean = false,
)