package com.rumble.battles.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.battles.commonViews.dialogs.AlertDialogResponseData
import com.rumble.battles.commonViews.dialogs.AlertDialogState
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.settings.domain.domainmodel.DebugAdType
import com.rumble.domain.settings.model.UserPreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

interface DebugAdSettingsHandler {
    val uiState: StateFlow<DebugAdSettingsUIState>
    val vmEvents: Flow<DebugAdSettingsScreenVmEvent>

    fun onDebugAdTypeChanged(value: DebugAdType)
    fun onCustomAdTagChanged(value: String)

    fun onUpdate()
    fun onDismissDialog()
}

sealed class DebugAdSettingsScreenVmEvent {
    data class Error(val errorMessage: String? = null) : DebugAdSettingsScreenVmEvent()
}

data class DebugAdSettingsUIState(
    val customAdTag: String,
    val debugAdType: DebugAdType,
    val customAdTagError: Boolean = false,
    val alertDialogState: AlertDialogState = AlertDialogState(),
    val alertDialogResponseData: AlertDialogResponseData? = null,
    val initialFetch: Boolean = true,
    val loading: Boolean = false,
)

private const val TAG = "DebugAdSettingsViewModel"

@HiltViewModel
class DebugAdSettingsViewModel @Inject constructor(
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val userPreferenceManager: UserPreferenceManager,
) : ViewModel(), DebugAdSettingsHandler {

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
        uiState.update { it.copy(loading = false) }
        emitVmEvent(DebugAdSettingsScreenVmEvent.Error())
    }

    private var customAdTag = ""
    private var debugAdType = DebugAdType.REAL_AD

    override val uiState =
        MutableStateFlow(
            DebugAdSettingsUIState(
                customAdTag = customAdTag,
                debugAdType = debugAdType
            )
        )

    private val _vmEvents = Channel<DebugAdSettingsScreenVmEvent>(capacity = Channel.CONFLATED)
    override val vmEvents: Flow<DebugAdSettingsScreenVmEvent> = _vmEvents.receiveAsFlow()

    init {
        viewModelScope.launch(errorHandler) {
            uiState.update { it.copy(loading = true) }
            val adType = userPreferenceManager.debugAdTypeFlow.first()
            val adTag = userPreferenceManager.customAdTagFlow.first()
            debugAdType = adType
            customAdTag = adTag
            uiState.update {
                it.copy(
                    debugAdType = adType,
                    customAdTag = adTag,
                    initialFetch = false,
                    loading = false
                )
            }
        }
    }

    override fun onDebugAdTypeChanged(value: DebugAdType) {
        debugAdType = value
        uiState.update {
            it.copy(
                debugAdType = value,
                customAdTagError = false
            )
        }
    }

    override fun onCustomAdTagChanged(value: String) {
        customAdTag = value
        uiState.update {
            it.copy(
                customAdTag = value,
                customAdTagError = false
            )
        }
    }

    override fun onUpdate() {
        if (validateInput(debugAdType, customAdTag)) {
            uiState.update {
                it.copy(
                    loading = true
                )
            }
            viewModelScope.launch(errorHandler) {
                userPreferenceManager.saveAdType(debugAdType)
                if (debugAdType == DebugAdType.CUSTOM_AD_TAG) {
                    userPreferenceManager.saveCustomAdTag(customAdTag)
                }
                uiState.update {
                    it.copy(
                        alertDialogState = AlertDialogState(true),
                        loading = false
                    )
                }
            }
        }
    }

    override fun onDismissDialog() {
        uiState.update {
            it.copy(
                alertDialogState = AlertDialogState(),
            )
        }
    }

    private fun validateInput(debugAdType: DebugAdType, customAdTag: String): Boolean {
        var validInput = true
        if (debugAdType == DebugAdType.CUSTOM_AD_TAG && customAdTag.isBlank()) {
            uiState.update {
                it.copy(
                    customAdTagError = true
                )
            }
            validInput = false
        }
        return validInput
    }


    private fun emitVmEvent(event: DebugAdSettingsScreenVmEvent) {
        _vmEvents.trySend(event)
    }
}