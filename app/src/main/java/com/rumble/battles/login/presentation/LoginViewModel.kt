package com.rumble.battles.login.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.battles.navigation.LandingPath
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.login.domain.usecases.RumbleLoginUseCase
import com.rumble.domain.profile.domain.GetUserProfileUseCase
import com.rumble.domain.validation.usecases.BirthdayValidationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginScreenError {
    data object None : LoginScreenError()
    data object InputError : LoginScreenError()
}

sealed class LoginScreenVmEvent {
    data class Error(val errorMessage: String? = null) : LoginScreenVmEvent()
    data object UserNamePasswordError : LoginScreenVmEvent()
    data object NavigateToHomeScreen : LoginScreenVmEvent()
    data class NavigateToAgeVerification(val onStartLogin: Boolean) : LoginScreenVmEvent()
    data object NavigateBack : LoginScreenVmEvent()
}

data class LoginScreenState(
    val loading: Boolean = false,
)

interface LoginHandler {
    val state: State<LoginScreenState>
    val userNameEmailError: State<LoginScreenError>
    val passwordError: State<LoginScreenError>
    val vmEvents: Flow<LoginScreenVmEvent>

    fun onUserNameChanged(value: String)
    fun onPasswordChanged(value: String)
    fun onSignIn()
}

private const val TAG = "LoginViewModel"

@HiltViewModel
class LoginViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val rumbleLoginUseCase: RumbleLoginUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val birthdayValidationUseCase: BirthdayValidationUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
) : ViewModel(), LoginHandler {

    private val onStartLogin = savedState.get<Boolean>(LandingPath.ON_START.path) ?: true
    private var currentState = LoginScreenState()
    private var userNameEmail = ""
    private var password = ""

    override val state: MutableState<LoginScreenState> = mutableStateOf(currentState)
    override val userNameEmailError: MutableState<LoginScreenError> =
        mutableStateOf(LoginScreenError.None)
    override val passwordError: MutableState<LoginScreenError> =
        mutableStateOf(LoginScreenError.None)

    private val _vmEvents = Channel<LoginScreenVmEvent>(capacity = Channel.CONFLATED)
    override val vmEvents: Flow<LoginScreenVmEvent> = _vmEvents.receiveAsFlow()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
        state.value = currentState.copy(loading = false)
        emitVmEvent(LoginScreenVmEvent.Error())
    }

    override fun onUserNameChanged(value: String) {
        userNameEmail = value
        userNameEmailError.value = LoginScreenError.None
    }

    override fun onPasswordChanged(value: String) {
        password = value
        passwordError.value = LoginScreenError.None
    }

    override fun onSignIn() {
        if (userNameEmail.isBlank()) {
            userNameEmailError.value = LoginScreenError.InputError
        }
        if (password.isBlank()) {
            passwordError.value = LoginScreenError.InputError
        }
        if (userNameEmail.isNotBlank() && password.isNotBlank()) {
            state.value = currentState.copy(loading = true)
            viewModelScope.launch(errorHandler) {
                if (rumbleLoginUseCase(
                        username = userNameEmail.trim(),
                        password = password
                    ).success
                ) {
                    /*TODO uncomment once age verification is added back*/
//                    // verify age restrictions
                    val profileResult = getUserProfileUseCase()
                    if (profileResult.success) {
                        val birthday = profileResult.userProfileEntity?.birthday?.toUtcLong()
                        if (birthday == null || birthdayValidationUseCase(birthday).first) {
                            emitVmEvent(LoginScreenVmEvent.NavigateToAgeVerification(onStartLogin))
                            return@launch
                        }
                        emitVmEvent(LoginScreenVmEvent.NavigateToAgeVerification(onStartLogin))
                        return@launch
                    }
                    handleNavigation()
                } else {
                    state.value = currentState.copy(loading = false)
                    emitVmEvent(LoginScreenVmEvent.UserNamePasswordError)
                }
            }
        }
    }

    private fun handleNavigation() {
        if (onStartLogin) {
            emitVmEvent(LoginScreenVmEvent.NavigateToHomeScreen)
        } else {
            emitVmEvent(LoginScreenVmEvent.NavigateBack)
        }
    }

    private fun emitVmEvent(event: LoginScreenVmEvent) {
        _vmEvents.trySend(event)
    }
}