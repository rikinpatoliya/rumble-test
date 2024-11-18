package com.rumble.battles.login.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.AccessToken
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.login.domain.domainmodel.LoginType
import com.rumble.domain.login.domain.usecases.SSOLoginUseCase
import com.rumble.domain.profile.domain.GetUserProfileUseCase
import com.rumble.domain.settings.domain.domainmodel.ColorMode
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.domain.validation.usecases.BirthdayValidationUseCase
import com.rumble.network.dto.login.UNABLE_TO_FIND_USER_ERROR
import com.rumble.utils.RumbleConstants.FACEBOOK_REGISTRATION_EMAIL_REQUEST_FIELD
import com.rumble.utils.extension.toUtcLong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AuthViewModel"

interface AuthHandler : FacebookCallback<LoginResult> {
    val state: State<AuthState>
    val eventFlow: Flow<AuthHandlerEvent>
    val googleSignInClient: GoogleSignInClient?
    val colorMode: Flow<ColorMode>

    fun onGoogleSignIn(task: Task<GoogleSignInAccount>)
    fun onFacebookTokenReceived(accessToken: AccessToken)
}

data class AuthState(
    val loading: Boolean = false,
    val uitTesting: Boolean = false,
)

sealed class AuthHandlerEvent {
    data class Error(val errorMessage: String? = null) : AuthHandlerEvent()
    data object NavigateToHomeScreen : AuthHandlerEvent()
    data class NavigateToRegistration(
        val loginType: LoginType,
        val userId: String,
        val token: String,
        val email: String
    ) : AuthHandlerEvent()

    data object NavigateToAgeVerification : AuthHandlerEvent()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    override val googleSignInClient: GoogleSignInClient?,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val ssoLoginUseCase: SSOLoginUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val birthdayValidationUseCase: BirthdayValidationUseCase,
    userPreferenceManager: UserPreferenceManager,
) : ViewModel(), AuthHandler {

    override val state: MutableState<AuthState> = mutableStateOf(AuthState())
    override val eventFlow: MutableSharedFlow<AuthHandlerEvent> = MutableSharedFlow()
    override val colorMode: Flow<ColorMode> = userPreferenceManager.colorMode

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
        state.value = state.value.copy(loading = false)
        emitEvent(AuthHandlerEvent.Error())
    }

    init {
        viewModelScope.launch(errorHandler) {
            userPreferenceManager.uitTestingModeFlow.distinctUntilChanged().collectLatest {
                state.value = state.value.copy(uitTesting = it)
            }
        }
    }

    override fun onGoogleSignIn(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            state.value = state.value.copy(loading = true)
            val account = task.getResult(ApiException::class.java)
            val userId = account.id ?: ""
            val token = account.idToken ?: ""
            viewModelScope.launch(errorHandler) {
                val result = ssoLoginUseCase(LoginType.GOOGLE, userId = userId, token = token)
                if (result.success) {
                    // for sso login, verify age restrictions
                    val profileResult = getUserProfileUseCase()
                    if (profileResult.success) {
                        val userProfile = profileResult.userProfileEntity
                        val birthday = userProfile?.birthday?.toUtcLong()
                        if (userProfile?.ageVerificationRequired == true &&
                            birthdayValidationUseCase(birthday, userProfile.minEligibleAge).first
                        ) {
                            state.value = state.value.copy(loading = false)
                            emitEvent(AuthHandlerEvent.NavigateToAgeVerification)
                            return@launch
                        }
                    }
                    state.value = state.value.copy(loading = false)
                    emitEvent(AuthHandlerEvent.NavigateToHomeScreen)
                } else if (result.error == UNABLE_TO_FIND_USER_ERROR) {
                    state.value = state.value.copy(loading = false)
                    emitEvent(
                        AuthHandlerEvent.NavigateToRegistration(
                            LoginType.GOOGLE,
                            userId,
                            token,
                            account.email ?: ""
                        )
                    )
                } else {
                    state.value = state.value.copy(loading = false)
                    emitEvent(AuthHandlerEvent.Error(result.error))
                }
            }
        } else {
            unhandledErrorUseCase(
                TAG,
                task.exception ?: Throwable(message = "GoogleSignInAccount task was not successful")
            )
            emitEvent(AuthHandlerEvent.Error())
        }
    }

    override fun onFacebookTokenReceived(accessToken: AccessToken) =
        onFacebookLogin(accessToken)

    override fun onCancel() {}//Nothing should be done if user cancelled intent

    override fun onError(error: FacebookException) {
        unhandledErrorUseCase(TAG, error.fillInStackTrace())
        emitEvent(AuthHandlerEvent.Error())
    }

    override fun onSuccess(result: LoginResult) {
        onFacebookTokenReceived(result.accessToken)
    }

    private fun onFacebookLogin(accessToken: AccessToken) {
        state.value = state.value.copy(loading = true)
        viewModelScope.launch(errorHandler) {
            if (ssoLoginUseCase(
                    LoginType.FACEBOOK,
                    userId = accessToken.userId,
                    token = accessToken.token
                ).success
            ) {
                // for sso login, verify age restrictions
                val profileResult = getUserProfileUseCase()
                if (profileResult.success) {
                    val userProfile = profileResult.userProfileEntity
                    val birthday = userProfile?.birthday?.toUtcLong()
                    if (userProfile?.ageVerificationRequired == true &&
                        birthdayValidationUseCase(birthday, userProfile.minEligibleAge).first) {
                        state.value = state.value.copy(loading = false)
                        emitEvent(AuthHandlerEvent.NavigateToAgeVerification)
                        return@launch
                    }
                }
                state.value = state.value.copy(loading = false)
                emitEvent(AuthHandlerEvent.NavigateToHomeScreen)
            } else {
                getUserFacebookEmail(accessToken)
            }
        }
    }

    private fun getUserFacebookEmail(accessToken: AccessToken) {
        GraphRequest(
            accessToken,
            accessToken.userId,
            bundleOf("fields" to FACEBOOK_REGISTRATION_EMAIL_REQUEST_FIELD),
            HttpMethod.GET,
            { graphResponse ->
                val email = graphResponse.getJSONObject()
                    ?.optString(FACEBOOK_REGISTRATION_EMAIL_REQUEST_FIELD)
                state.value = state.value.copy(loading = false)
                emitEvent(
                    AuthHandlerEvent.NavigateToRegistration(
                        LoginType.FACEBOOK,
                        accessToken.userId,
                        accessToken.token,
                        email ?: ""
                    )
                )
            }
        ).executeAsync()
    }

    private fun emitEvent(event: AuthHandlerEvent) {
        viewModelScope.launch {
            eventFlow.emit(event)
        }
    }
}
