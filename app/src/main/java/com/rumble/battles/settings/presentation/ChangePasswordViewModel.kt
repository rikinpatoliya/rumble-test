package com.rumble.battles.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.battles.commonViews.dialogs.AlertDialogResponseData
import com.rumble.battles.commonViews.dialogs.AlertDialogState
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.settings.domain.usecase.UpdatePasswordUseCase
import com.rumble.domain.validation.usecases.PasswordValidationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ChangePasswordHandler {
    val uiState: StateFlow<ChangePasswordUIState>
    val vmEvents: Flow<ChangePasswordScreenVmEvent>

    fun onNewPasswordChanged(value: String)
    fun onCurrentPasswordChanged(value: String)

    fun onUpdate()
    fun onDismissDialog()

}

data class ChangePasswordUIState(
    val newPassword: String,
    val currentPassword: String,
    val newPasswordError: Boolean = false,
    val currentPasswordError: Boolean = false,
    val alertDialogState: AlertDialogState = AlertDialogState(),
    val alertDialogResponseData: AlertDialogResponseData? = null,
    val loading: Boolean = false,
)

sealed class ChangePasswordScreenVmEvent {
    data class Error(val errorMessage: String? = null) : ChangePasswordScreenVmEvent()
}

private const val TAG = "ChangePasswordViewModel"

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    private val passwordValidationUseCase: PasswordValidationUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
) : ViewModel(), ChangePasswordHandler {

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
        uiState.update { it.copy(loading = false) }
        emitVmEvent(ChangePasswordScreenVmEvent.Error())
    }


    override val uiState = MutableStateFlow(ChangePasswordUIState("", ""))

    private val _vmEvents = Channel<ChangePasswordScreenVmEvent>(capacity = Channel.CONFLATED)
    override val vmEvents: Flow<ChangePasswordScreenVmEvent> = _vmEvents.receiveAsFlow()

    override fun onNewPasswordChanged(value: String) {
        uiState.update {
            it.copy(
                newPassword = value.trim(),
                newPasswordError = false
            )
        }
    }

    override fun onCurrentPasswordChanged(value: String) {
        uiState.update {
            it.copy(
                currentPassword = value.trim(),
                currentPasswordError = false
            )
        }
    }

    override fun onUpdate() {
        if (validInput(uiState.value.newPassword, uiState.value.currentPassword)) {
            uiState.update {
                it.copy(loading = true)
            }
            viewModelScope.launch(errorHandler) {
                val result =
                    updatePasswordUseCase(
                        newPassword = uiState.value.newPassword,
                        currentPassword = uiState.value.currentPassword
                    )
                uiState.update {
                    it.copy(
                        loading = false,
                        alertDialogResponseData = AlertDialogResponseData(
                            result.success,
                            result.message
                        ),
                        alertDialogState = AlertDialogState(true)
                    )
                }
            }
        }
    }

    override fun onDismissDialog() {
        uiState.update {
            it.copy(
                alertDialogState = AlertDialogState(),
                alertDialogResponseData = null
            )
        }
    }

    private fun validInput(newPassword: String, currentPassword: String): Boolean {
        var validInput = true
        if (!passwordValidationUseCase(newPassword)) {
            uiState.update {
                it.copy(
                    newPasswordError = true,
                )
            }
            validInput = false
        }
        if (currentPassword.isEmpty()) {
            uiState.update {
                it.copy(
                    currentPasswordError = true
                )
            }
            validInput = false
        }
        return validInput
    }

    private fun emitVmEvent(event: ChangePasswordScreenVmEvent) {
        _vmEvents.trySend(event)
    }
}