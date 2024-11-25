package com.rumble.battles.login.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.dialogs.AlertDialogState
import com.rumble.battles.navigation.LandingPath
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.common.domain.usecase.AnnotatedStringUseCase
import com.rumble.domain.common.domain.usecase.AnnotatedStringWithActionsList
import com.rumble.domain.common.domain.usecase.SendEmailUseCase
import com.rumble.domain.login.domain.domainmodel.LoginType
import com.rumble.domain.login.domain.domainmodel.RegisterResult
import com.rumble.domain.login.domain.usecases.RegisterUseCase
import com.rumble.domain.login.domain.usecases.RumbleFormBodyBuilderUseCase
import com.rumble.domain.login.domain.usecases.RumbleLoginUseCase
import com.rumble.domain.login.domain.usecases.SSOFormBodyBuilderUseCase
import com.rumble.domain.login.domain.usecases.SSOLoginUseCase
import com.rumble.domain.profile.domainmodel.Gender
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.domain.validation.usecases.BirthdayValidationUseCase
import com.rumble.domain.validation.usecases.EmailValidationUseCase
import com.rumble.domain.validation.usecases.PasswordValidationUseCase
import com.rumble.domain.validation.usecases.UserNameValidationUseCase
import com.rumble.network.session.SessionManager
import com.rumble.utils.RumbleConstants
import com.rumble.utils.RumbleConstants.API_FORMAT_DATE_PATTERN
import com.rumble.utils.errors.InputValidationError
import com.rumble.utils.extension.convertToDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

interface RegisterHandler {
    val uiState: StateFlow<RegistrationScreenUIState>
    val alertDialogState: StateFlow<AlertDialogState>
    val vmEvents: Flow<RegistrationScreenVmEvent>

    fun onUserNameChanged(value: String)
    fun onPasswordChanged(value: String)
    fun onEmailChanged(value: String)
    fun onBirthdayChanged(value: Long)
    fun onTermsAndConditionCheckedChanged(value: Boolean)
    fun onWhyWeAskBirthdayClicked()
    fun onDismissDialog()
    fun onSendEmail(email: String)
    fun onAnnotatedTextClicked(
        annotatedTextWithActions: AnnotatedStringWithActionsList,
        offset: Int
    )

    fun onOpenUri(tag: String, uri: String)
    fun onJoin()
    fun onGenderSelected(gender: Gender)
}

sealed class RegistrationScreenVmEvent {
    data class Error(val errorMessage: String? = null) : RegistrationScreenVmEvent()
    data object NavigateToHomeScreen : RegistrationScreenVmEvent()
    data object NavigateToAgeVerification : RegistrationScreenVmEvent()
    data class NavigateToWebView(val url: String) : RegistrationScreenVmEvent()
}

sealed class RegistrationScreenAlertDialogReason : AlertDialogReason {
    object WhyWeAskBirthdayDialogReason : RegistrationScreenAlertDialogReason()
}

data class RegistrationScreenUIState(
    val userRegistrationEntity: UserRegistrationEntity,
    val ssoRegistration: Boolean,
    val alertDialogState: AlertDialogState = AlertDialogState(),
    val loading: Boolean = false,
    val usernameError: Pair<Boolean, InputValidationError> = Pair(
        false,
        InputValidationError.None
    ),
    val passwordError: Boolean = false,
    val emailError: Boolean = false,
    val birthdayError: Pair<Boolean, InputValidationError> = Pair(
        false,
        InputValidationError.None
    ),
    val termsError: Boolean = false,
    val gender: Gender = Gender.Unspecified
)

data class UserRegistrationEntity(
    val loginType: LoginType = LoginType.UNKNOWN,
    val userName: String = "",
    val email: String = "",
    val birthday: Long = 0L,
    val password: String = "",
    val termsAccepted: Boolean = false,
    val userId: String = "",
    val token: String = "",
    val gender: String = "",
)

private const val TAG = "RegisterViewModel"

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userPreferenceManager: UserPreferenceManager,
    private val registerUseCase: RegisterUseCase,
    private val ssoFormBodyBuilderUseCase: SSOFormBodyBuilderUseCase,
    private val rumbleFormBodyBuilderUseCase: RumbleFormBodyBuilderUseCase,
    private val ssoLoginUseCase: SSOLoginUseCase,
    private val rumbleLoginUseCase: RumbleLoginUseCase,
    private val emailValidationUseCase: EmailValidationUseCase,
    private val userNameValidationUseCase: UserNameValidationUseCase,
    private val passwordValidationUseCase: PasswordValidationUseCase,
    private val birthdayValidationUseCase: BirthdayValidationUseCase,
    private val sendEmailUseCase: SendEmailUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val annotatedStringUseCase: AnnotatedStringUseCase,
    private val sessionManager: SessionManager,
    stateHandle: SavedStateHandle
) : ViewModel(), RegisterHandler {

    private var userRegistrationEntity = UserRegistrationEntity()
    private var minEligibleAge: Int? = null

    override val uiState = MutableStateFlow(createDefaultUiState(stateHandle))
    override val alertDialogState = MutableStateFlow(AlertDialogState())

    private val _vmEvents = Channel<RegistrationScreenVmEvent>(capacity = Channel.CONFLATED)
    override val vmEvents: Flow<RegistrationScreenVmEvent> = _vmEvents.receiveAsFlow()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
        uiState.update { it.copy(loading = false) }
        emitVmEvent(RegistrationScreenVmEvent.Error())
    }

    init {
        viewModelScope.launch {
            //   userPreferenceManager.saveColorMode(ColorMode.DARK_MODE)
            minEligibleAge = sessionManager.minEligibleAgeFlow.first()
        }
    }

    override fun onUserNameChanged(value: String) {
        userRegistrationEntity = userRegistrationEntity.copy(
            userName = value
        )
        uiState.update {
            it.copy(
                userRegistrationEntity = userRegistrationEntity,
                usernameError = Pair(false, InputValidationError.None)
            )
        }
    }

    override fun onPasswordChanged(value: String) {
        userRegistrationEntity = userRegistrationEntity.copy(
            password = value
        )
        uiState.update {
            it.copy(
                userRegistrationEntity = userRegistrationEntity,
                passwordError = false
            )
        }
    }

    override fun onEmailChanged(value: String) {
        userRegistrationEntity = userRegistrationEntity.copy(
            email = value.trim()
        )
        uiState.update {
            it.copy(
                userRegistrationEntity = userRegistrationEntity,
                emailError = false
            )
        }
    }

    override fun onBirthdayChanged(value: Long) {
        userRegistrationEntity = userRegistrationEntity.copy(
            birthday = value
        )
        uiState.update {
            it.copy(
                userRegistrationEntity = userRegistrationEntity,
                birthdayError = Pair(false, InputValidationError.None)
            )
        }
    }

    override fun onTermsAndConditionCheckedChanged(value: Boolean) {
        userRegistrationEntity = userRegistrationEntity.copy(
            termsAccepted = value
        )
        uiState.update {
            it.copy(
                userRegistrationEntity = userRegistrationEntity,
                termsError = false
            )
        }
    }

    override fun onWhyWeAskBirthdayClicked() {
        alertDialogState.value = AlertDialogState(
            show = true,
            alertDialogReason = RegistrationScreenAlertDialogReason.WhyWeAskBirthdayDialogReason
        )
    }

    override fun onDismissDialog() {
        alertDialogState.value = AlertDialogState()
    }

    override fun onAnnotatedTextClicked(
        annotatedTextWithActions: AnnotatedStringWithActionsList,
        offset: Int
    ) {
        annotatedStringUseCase.invoke(annotatedTextWithActions, offset)
    }

    override fun onOpenUri(tag: String, uri: String) {
        emitVmEvent(RegistrationScreenVmEvent.NavigateToWebView(uri))
    }

    override fun onJoin() {
        if (validInput(userRegistrationEntity)) {
            viewModelScope.launch(errorHandler) {
                uiState.update { it.copy(loading = true) }
                val registerResult: RegisterResult = registerUseCase(
                    userRegistrationEntity.loginType,
                    if (userRegistrationEntity.loginType == LoginType.RUMBLE) rumbleFormBodyBuilderUseCase(
                        userRegistrationEntity.userName,
                        userRegistrationEntity.password,
                        userRegistrationEntity.email,
                        userRegistrationEntity.birthday.convertToDate(pattern = API_FORMAT_DATE_PATTERN),
                        userRegistrationEntity.termsAccepted,
                        userRegistrationEntity.gender
                    ) else ssoFormBodyBuilderUseCase(
                        userRegistrationEntity.loginType,
                        userRegistrationEntity.userName,
                        userRegistrationEntity.email,
                        userRegistrationEntity.userId,
                        userRegistrationEntity.token,
                        userRegistrationEntity.birthday.convertToDate(pattern = API_FORMAT_DATE_PATTERN),
                        userRegistrationEntity.termsAccepted
                    )
                )
                when (registerResult) {
                    is RegisterResult.Success -> {
                        if (userRegistrationEntity.loginType == LoginType.FACEBOOK) {
                            delay(RumbleConstants.FACEBOOK_REGISTRATION_TO_FACEBOOK_LOGIN_DELAY)
                        }
                        val loginResult =
                            if (userRegistrationEntity.loginType == LoginType.RUMBLE) rumbleLoginUseCase(
                                username = userRegistrationEntity.userName,
                                password = userRegistrationEntity.password,
                            ) else ssoLoginUseCase(
                                loginType = userRegistrationEntity.loginType,
                                userId = userRegistrationEntity.userId,
                                token = userRegistrationEntity.token,
                            )
                        if (loginResult.success) {
                            uiState.update {
                                it.copy(
                                    loading = false
                                )
                            }
                            emitVmEvent(RegistrationScreenVmEvent.NavigateToHomeScreen)
                        } else {
                            emitVmEvent(RegistrationScreenVmEvent.Error(errorMessage = loginResult.error))
                        }
                    }

                    is RegisterResult.Failure -> {
                        uiState.update {
                            it.copy(
                                loading = false
                            )
                        }
                        emitVmEvent(RegistrationScreenVmEvent.Error(errorMessage = registerResult.errorMessage))
                    }

                    is RegisterResult.DuplicatedRequest -> {
                        uiState.update {
                            it.copy(
                                loading = false
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onSendEmail(email: String) {
        sendEmailUseCase(email)
    }

    override fun onGenderSelected(gender: Gender) {
        userRegistrationEntity = userRegistrationEntity.copy(
            gender = gender.requestValue
        )
        uiState.update {
            it.copy(gender = gender)
        }
    }

    private fun createDefaultUiState(stateHandle: SavedStateHandle): RegistrationScreenUIState {
        val loginType = LoginType.getByValue(
            stateHandle.get<String>(LandingPath.LOGINTYPE.path)?.toIntOrNull() ?: 0
        )
        userRegistrationEntity = userRegistrationEntity.copy(
            loginType = loginType
        )
        val userId = stateHandle.get<String>(LandingPath.USERID.path) ?: ""
        val token = stateHandle.get<String>(LandingPath.TOKEN.path) ?: ""
        val email = stateHandle.get<String>(LandingPath.EMAIL.path) ?: ""
        val ssoRegistration = loginType == LoginType.GOOGLE
            || loginType == LoginType.FACEBOOK
            || loginType == LoginType.APPLE
        if (ssoRegistration) {
            userRegistrationEntity = userRegistrationEntity.copy(
                email = email,
                userId = userId,
                token = token
            )
        }
        return RegistrationScreenUIState(
            userRegistrationEntity = userRegistrationEntity,
            ssoRegistration = ssoRegistration
        )
    }

    private fun validInput(userRegistrationEntity: UserRegistrationEntity): Boolean {
        var validInput = true
        val userNameError = userNameValidationUseCase(userRegistrationEntity.userName)
        if (userNameError.first) {
            uiState.update {
                it.copy(
                    usernameError = userNameError
                )
            }
            validInput = false
        }
        if (!uiState.value.ssoRegistration) {
            if (!passwordValidationUseCase(uiState.value.userRegistrationEntity.password)) {
                uiState.update {
                    it.copy(
                        passwordError = true
                    )
                }
                validInput = false
            }
        }
        if (!emailValidationUseCase(userRegistrationEntity.email)) {
            uiState.update {
                it.copy(
                    emailError = true
                )
            }
            validInput = false
        }
        val birthdayError = birthdayValidationUseCase(
            userRegistrationEntity.birthday,
            minEligibleAge ?: RumbleConstants.MINIMUM_AGE_REQUIREMENT
        )
        if (birthdayError.first) {
            uiState.update {
                it.copy(
                    birthdayError = birthdayError
                )
            }
            validInput = false
        }
        if (!userRegistrationEntity.termsAccepted) {
            uiState.update {
                it.copy(
                    termsError = true
                )
            }
            validInput = false
        }
        return validInput
    }

    private fun emitVmEvent(event: RegistrationScreenVmEvent) {
        _vmEvents.trySend(event)
    }
}