package com.rumble.battles.login.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.dialogs.AlertDialogState
import com.rumble.battles.navigation.LandingPath
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.common.domain.usecase.AnnotatedStringUseCase
import com.rumble.domain.common.domain.usecase.AnnotatedStringWithActionsList
import com.rumble.domain.common.domain.usecase.SendEmailUseCase
import com.rumble.domain.login.domain.usecases.SaveAgeVerifiedStatusUseCase
import com.rumble.domain.profile.domain.GetUserProfileUseCase
import com.rumble.domain.profile.domain.SignOutUseCase
import com.rumble.domain.profile.domain.UpdateUserProfileUseCase
import com.rumble.domain.profile.domainmodel.CountryEntity
import com.rumble.domain.profile.domainmodel.Gender
import com.rumble.domain.profile.domainmodel.UserProfileEntity
import com.rumble.domain.settings.domain.domainmodel.ColorMode
import com.rumble.domain.settings.domain.domainmodel.UpdateUserProfileResult
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.domain.validation.usecases.BirthdayValidationUseCase
import com.rumble.network.session.SessionManager
import com.rumble.utils.errors.InputValidationError
import com.rumble.utils.extension.toUtcLong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import javax.inject.Inject

interface AgeVerificationHandler {
    val uiState: StateFlow<AgeVerificationScreenUIState>
    val alertDialogState: StateFlow<AlertDialogState>
    val vmEvents: Flow<AgeVerificationScreenVmEvent>
    fun onBirthdayChanged(value: LocalDate)
    fun onWhyWeAskBirthdayClicked()
    fun onSendEmail(email: String)
    fun onDismissDialog()
    fun onAnnotatedTextClicked(
        annotatedTextWithActions: AnnotatedStringWithActionsList,
        offset: Int
    )

    fun onOpenUri(tag: String, uri: String)
    fun onUpdateBirthday()
    fun saveAgeNotVerifiedState()
}

sealed class AgeVerificationScreenVmEvent {
    data class Error(val errorMessage: String? = null) : AgeVerificationScreenVmEvent()
    object NavigateToHomeScreen : AgeVerificationScreenVmEvent()
    object NavigateBack : AgeVerificationScreenVmEvent()
    data class NavigateToWebView(val url: String) : AgeVerificationScreenVmEvent()
}

sealed class AgeVerificationScreenAlertDialogReason : AlertDialogReason {
    object WhyWeAskBirthdayDialogReason : AgeVerificationScreenAlertDialogReason()
}

data class AgeVerificationScreenUIState(
    val userProfileEntity: UserProfileEntity,
    val alertDialogState: AlertDialogState = AlertDialogState(),
    val loading: Boolean = false,
    val birthdayError: Pair<Boolean, InputValidationError> = Pair(
        false,
        InputValidationError.None
    )
)

private const val TAG = "AgeVerificationViewModel"

@HiltViewModel
class AgeVerificationViewModel @Inject constructor(
    private val userPreferenceManager: UserPreferenceManager,
    private val birthdayValidationUseCase: BirthdayValidationUseCase,
    private val sendEmailUseCase: SendEmailUseCase,
    private val annotatedStringUseCase: AnnotatedStringUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val saveAgeVerifiedStatusUseCase: SaveAgeVerifiedStatusUseCase,
    stateHandle: SavedStateHandle
) : ViewModel(), AgeVerificationHandler {

    private val onStartLogin = stateHandle.get<Boolean>(LandingPath.ON_START.path) ?: true

    private var userProfileEntity: UserProfileEntity =
        UserProfileEntity(
            "",
            "",
            "",
            false,
            "",
            "",
            "",
            "",
            "",
            "",
            CountryEntity(0, ""),
            "",
            0,
            false,
            Gender.Unspecified,
            null
        )


    override val uiState = MutableStateFlow(
        AgeVerificationScreenUIState(userProfileEntity = userProfileEntity)
    )


    override val alertDialogState = MutableStateFlow(AlertDialogState())

    private val _vmEvents = Channel<AgeVerificationScreenVmEvent>(capacity = Channel.CONFLATED)
    override val vmEvents: Flow<AgeVerificationScreenVmEvent> = _vmEvents.receiveAsFlow()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
        uiState.update { it.copy(loading = false) }
        emitVmEvent(AgeVerificationScreenVmEvent.Error())
    }

    init {
        viewModelScope.launch(errorHandler) {
            uiState.update { it.copy(loading = true) }
            val getUserProfileResult = getUserProfileUseCase()
            if (getUserProfileResult.success && getUserProfileResult.userProfileEntity != null) {
                getUserProfileResult.userProfileEntity?.let { userProfile ->
                    userProfileEntity = userProfile
                    uiState.update {
                        it.copy(
                            userProfileEntity = userProfile,
                            loading = false
                        )
                    }
                }
            } else {
                uiState.update { it.copy(loading = false) }
            }
        }

        viewModelScope.launch {
            userPreferenceManager.saveColorMode(ColorMode.DARK_MODE)
        }
    }

    override fun onCleared() {
        runBlocking {
            userPreferenceManager.saveColorMode(ColorMode.SYSTEM_DEFAULT)
        }
        super.onCleared()
    }


    override fun onBirthdayChanged(value: LocalDate) {
        userProfileEntity = userProfileEntity.copy(
            birthday = value
        )
        uiState.update {
            it.copy(
                userProfileEntity = userProfileEntity,
                birthdayError = Pair(false, InputValidationError.None)
            )
        }
    }

    override fun onWhyWeAskBirthdayClicked() {
        alertDialogState.value = AlertDialogState(
            show = true,
            alertDialogReason = AgeVerificationScreenAlertDialogReason.WhyWeAskBirthdayDialogReason
        )
    }

    override fun onSendEmail(email: String) {
        sendEmailUseCase(email)
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
        emitVmEvent(AgeVerificationScreenVmEvent.NavigateToWebView(uri))
    }

    override fun onUpdateBirthday() {
        if (validInput(userProfileEntity)) {
            viewModelScope.launch(errorHandler) {
                uiState.update { it.copy(loading = true) }
                when (val updateUserProfileResult = updateUserProfileUseCase(userProfileEntity)) {
                    is UpdateUserProfileResult.Success -> {
                        uiState.update { it.copy(loading = false) }
                        if (onStartLogin) {
                            emitVmEvent(AgeVerificationScreenVmEvent.NavigateToHomeScreen)
                        } else {
                            emitVmEvent(AgeVerificationScreenVmEvent.NavigateBack)
                        }
                    }

                    is UpdateUserProfileResult.Error -> {
                        uiState.update { it.copy(loading = false) }
                        emitVmEvent(
                            AgeVerificationScreenVmEvent.Error(
                                errorMessage = updateUserProfileResult.rumbleError.message
                            )
                        )
                    }

                    is UpdateUserProfileResult.FormError -> {
                        uiState.update { it.copy(loading = false) }
                        emitVmEvent(
                            AgeVerificationScreenVmEvent.Error(
                                errorMessage = updateUserProfileResult.birthdayErrorMessage
                            )
                        )
                    }
                }
            }
        }
    }

    override fun saveAgeNotVerifiedState() {
        viewModelScope.launch {
            saveAgeVerifiedStatusUseCase(false)
        }
    }

    private fun validInput(userProfileEntity: UserProfileEntity): Boolean {
        var validInput = true
        val birthday = userProfileEntity.birthday
        if (birthday != null) {
            val birthdayError = birthdayValidationUseCase(birthday.toUtcLong())
            if (birthdayError.first) {
                uiState.update {
                    it.copy(
                        birthdayError = birthdayError
                    )
                }
                validInput = false
            }
        }

        return validInput
    }

    private fun emitVmEvent(event: AgeVerificationScreenVmEvent) {
        _vmEvents.trySend(event)
    }
}