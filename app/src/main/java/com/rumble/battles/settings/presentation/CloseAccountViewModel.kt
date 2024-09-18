package com.rumble.battles.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.battles.commonViews.dialogs.AlertDialogResponseData
import com.rumble.battles.commonViews.dialogs.AlertDialogState
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.settings.domain.usecase.CloseAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

interface CloseAccountHandler {
    val uiState: StateFlow<CloseAccountUIState>
    val vmEvents: Flow<CloseAccountVmEvent>

    fun onProceed()
    fun onCloseAccount()
    fun onDismissDialog()
}

sealed class CloseAccountVmEvent {
    data class Error(val errorMessage: String? = null) : CloseAccountVmEvent()
}

data class CloseAccountUIState(
    val alertDialogState: AlertDialogState = AlertDialogState(),
    val alertDialogResponseData: AlertDialogResponseData? = null,
    val loading: Boolean = false,
)

private const val TAG = "CloseAccountViewModel"

@HiltViewModel
class CloseAccountViewModel @Inject constructor(
    private val closeAccountUseCase: CloseAccountUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
) : ViewModel(), CloseAccountHandler {

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
        uiState.update { it.copy(loading = false) }
        emitVmEvent(CloseAccountVmEvent.Error())
    }

    override val uiState = MutableStateFlow(CloseAccountUIState())

    private val _vmEvents = Channel<CloseAccountVmEvent>(capacity = Channel.CONFLATED)
    override val vmEvents: Flow<CloseAccountVmEvent> = _vmEvents.receiveAsFlow()

    override fun onProceed() {
        uiState.update {
            it.copy(
                alertDialogState = AlertDialogState(true),
            )
        }
    }

    override fun onCloseAccount() {
        uiState.update {
            it.copy(
                alertDialogState = AlertDialogState(),
                loading = true
            )
        }
        viewModelScope.launch(errorHandler) {
            val result = closeAccountUseCase()
            uiState.update {
                it.copy(
                    loading = false,
                    alertDialogResponseData = AlertDialogResponseData(
                        result.success,
                        null
                    ),
                    alertDialogState = AlertDialogState(true)
                )
            }
        }
    }

    override fun onDismissDialog() {
        uiState.update {
            it.copy(
                alertDialogState = AlertDialogState(),
                alertDialogResponseData = null,
            )
        }
    }

    private fun emitVmEvent(event: CloseAccountVmEvent) {
        _vmEvents.trySend(event)
    }
}