package com.rumble.battles.login.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.login.domain.domainmodel.ResetPasswordResult
import com.rumble.domain.login.domain.usecases.ResetPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PasswordResetState(
    val loading: Boolean = false,
)

sealed class UserOrEmailError {
    object None : UserOrEmailError()
    object CanNotBeEmptyError : UserOrEmailError()
    data class Error(val message: String = "") : UserOrEmailError()
}

interface PasswordResetHandler {
    val vmEvents: Flow<PasswordResetVmEvent>
    val state: State<PasswordResetState>
    val userNameEmailError: State<UserOrEmailError>

    fun onSubmit()
    fun onUserOrEmailChanged(value: String)
}

sealed class PasswordResetVmEvent {
    object ShowSuccess : PasswordResetVmEvent()
}

private const val TAG = "PasswordResetViewModel"

@HiltViewModel
class PasswordResetViewModel @Inject constructor(
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
) :
    ViewModel(), PasswordResetHandler {
    override val state: MutableState<PasswordResetState> = mutableStateOf(PasswordResetState())
    override val userNameEmailError: MutableState<UserOrEmailError> =
        mutableStateOf(UserOrEmailError.None)

    private var userNameOrEmail = ""

    private val _vmEvents = Channel<PasswordResetVmEvent>(capacity = Channel.CONFLATED)
    override val vmEvents: Flow<PasswordResetVmEvent> = _vmEvents.receiveAsFlow()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
        state.value = state.value.copy(loading = false)
        userNameEmailError.value = UserOrEmailError.Error()
    }

    override fun onSubmit() {
        if (userNameOrEmail.isNotEmpty()) {
            submitPasswordReset(userNameOrEmail)
        } else {
            userNameEmailError.value = UserOrEmailError.CanNotBeEmptyError
        }
    }

    private fun submitPasswordReset(userNameOrEmail: String) {
        viewModelScope.launch(errorHandler) {
            userNameEmailError.value = UserOrEmailError.None
            state.value = state.value.copy(loading = true)
            when (val result = resetPasswordUseCase(userNameOrEmail)) {
                is ResetPasswordResult.Failure -> {
                    userNameEmailError.value = UserOrEmailError.Error(result.rumbleError.message)
                }
                ResetPasswordResult.Success -> {
                    _vmEvents.trySend(PasswordResetVmEvent.ShowSuccess)
                }
            }
            state.value = state.value.copy(loading = false)
        }
    }

    override fun onUserOrEmailChanged(value: String) {
        userNameEmailError.value = UserOrEmailError.None
        userNameOrEmail = value
    }
}