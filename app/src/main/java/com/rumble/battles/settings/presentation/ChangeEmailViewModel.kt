package com.rumble.battles.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.battles.commonViews.dialogs.AlertDialogResponseData
import com.rumble.battles.commonViews.dialogs.AlertDialogState
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.profile.domain.GetUserProfileUseCase
import com.rumble.domain.settings.domain.usecase.UpdateEmailUseCase
import com.rumble.domain.validation.usecases.EmailValidationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ChangeEmailHandler {
    val uiState: StateFlow<EmailUIState>
    val vmEvents: Flow<ChangeEmailScreenVmEvent>

    fun onEmailChanged(value: String)
    fun onPasswordChanged(value: String)
    fun onDismissDialog()

    fun onUpdate()
}

sealed class EmailError {
    object None : EmailError()
    object Invalid : EmailError()
    object SameAsCurrent : EmailError()
}

sealed class ChangeEmailScreenVmEvent {
    data class Error(val errorMessage: String? = null) : ChangeEmailScreenVmEvent()
}

data class EmailUIState(
    val email: String,
    val password: String,
    val oldEmail: String = "",
    val emailError: Boolean = false,
    val emailErrorType: EmailError = EmailError.None,
    val passwordError: Boolean = false,
    val alertDialogState: AlertDialogState = AlertDialogState(),
    val alertDialogResponseData: AlertDialogResponseData? = null,
    val loading: Boolean = false,
)

private const val TAG = "ChangeEmailViewModel"

@HiltViewModel
class ChangeEmailViewModel @Inject constructor(
    private val updateEmailUseCase: UpdateEmailUseCase,
    private val emailValidationUseCase: EmailValidationUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    getUserProfileUseCase: GetUserProfileUseCase,
) : ViewModel(), ChangeEmailHandler {

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
        uiState.update { it.copy(loading = false) }
        emitVmEvent(ChangeEmailScreenVmEvent.Error())
    }

    override val uiState = MutableStateFlow(EmailUIState("", ""))

    private val _vmEvents = Channel<ChangeEmailScreenVmEvent>(capacity = Channel.CONFLATED)
    override val vmEvents: Flow<ChangeEmailScreenVmEvent> = _vmEvents.receiveAsFlow()

    init {
        viewModelScope.launch(errorHandler) {
            val getUserProfileResult = getUserProfileUseCase()
            if (getUserProfileResult.success && getUserProfileResult.userProfileEntity != null) {
                getUserProfileResult.userProfileEntity?.let { userProfile ->
                    uiState.update {
                        it.copy(
                            oldEmail = userProfile.email
                        )
                    }
                }
            } else {
                uiState.update { it.copy(loading = false) }
                emitVmEvent(ChangeEmailScreenVmEvent.Error())
            }
        }
    }

    override fun onEmailChanged(value: String) {
        uiState.update {
            it.copy(
                email = value.trim(),
                emailError = false
            )
        }
    }

    override fun onPasswordChanged(value: String) {
        uiState.update {
            it.copy(
                password = value,
                passwordError = false
            )
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

    override fun onUpdate() {
        if (validInput(uiState.value)) {
            uiState.update {
                it.copy(
                    loading = true
                )
            }
            viewModelScope.launch(errorHandler) {
                val result =
                    updateEmailUseCase(
                        email = uiState.value.email,
                        password = uiState.value.password
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

    private fun validInput(state: EmailUIState): Boolean {
        var validInput = true
        if (!emailValidationUseCase(state.email)) {
            uiState.update {
                it.copy(
                    emailError = true,
                    emailErrorType = EmailError.Invalid
                )
            }
            validInput = false
        } else if (state.email.contentEquals(state.oldEmail, ignoreCase = true)) {
            uiState.update {
                it.copy(
                    emailError = true,
                    emailErrorType = EmailError.SameAsCurrent
                )
            }
            validInput = false
        }
        if (state.password.isEmpty()) {
            uiState.update {
                it.copy(
                    passwordError = true
                )
            }
            validInput = false
        }
        return validInput
    }

    private fun emitVmEvent(event: ChangeEmailScreenVmEvent) {
        _vmEvents.trySend(event)
    }
}