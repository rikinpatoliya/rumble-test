package com.rumble.battles.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.battles.commonViews.dialogs.AlertDialogResponseData
import com.rumble.battles.commonViews.dialogs.AlertDialogState
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.settings.domain.usecase.UpdateSubdomainUseCase
import com.rumble.network.subdomain.RumbleSubdomainUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ChangeSubdomainHandler {
    val uiState: StateFlow<ChangeSubdomainUIState>
    val vmEvents: Flow<ChangeSubdomainScreenVmEvent>

    fun onSubdomainChanged(value: String)

    fun onUpdate()
    fun onDismissDialog()
}

sealed class ChangeSubdomainScreenVmEvent {
    data class Error(val errorMessage: String? = null) : ChangeSubdomainScreenVmEvent()
}

data class ChangeSubdomainUIState(
    val subdomain: String,
    val alertDialogState: AlertDialogState = AlertDialogState(),
    val alertDialogResponseData: AlertDialogResponseData? = null,
    val loading: Boolean = false,
)

private const val TAG = "ChangeSubdomainViewModel"

@HiltViewModel
class ChangeSubdomainViewModel @Inject constructor(
    private val rumbleSubdomainUseCase: RumbleSubdomainUseCase,
    private val updateSubdomainUseCase: UpdateSubdomainUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
) : ViewModel(), ChangeSubdomainHandler {

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
        uiState.update { it.copy(loading = false) }
        emitVmEvent(ChangeSubdomainScreenVmEvent.Error())
    }

    override val uiState = MutableStateFlow(ChangeSubdomainUIState(""))

    private val _vmEvents = Channel<ChangeSubdomainScreenVmEvent>(capacity = Channel.CONFLATED)
    override val vmEvents: Flow<ChangeSubdomainScreenVmEvent> = _vmEvents.receiveAsFlow()

    init {
        viewModelScope.launch(errorHandler) {
            uiState.update {
                it.copy(loading = true)
            }
            val rumbleSubdomainResult = rumbleSubdomainUseCase()
            uiState.update {
                it.copy(
                    subdomain = rumbleSubdomainResult.userInitiatedSubdomain ?: rumbleSubdomainResult.appSubdomain ?: rumbleSubdomainResult.environmentSubdomain,
                    loading = false
                )
            }
        }
    }

    override fun onSubdomainChanged(value: String) {
        uiState.update {
            it.copy(
                subdomain = value.trim(),
            )
        }
    }

    override fun onUpdate() {
        uiState.update {
            it.copy(
                loading = true
            )
        }
        viewModelScope.launch(errorHandler) {
            updateSubdomainUseCase(uiState.value.subdomain)
            uiState.update {
                it.copy(
                    alertDialogState = AlertDialogState(true),
                    loading = false
                )
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

    private fun emitVmEvent(event: ChangeSubdomainScreenVmEvent) {
        _vmEvents.trySend(event)
    }
}