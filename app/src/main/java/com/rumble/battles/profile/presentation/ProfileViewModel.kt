package com.rumble.battles.profile.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.dialogs.AlertDialogState
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.camera.UploadStatus
import com.rumble.domain.camera.UploadVideoEntity
import com.rumble.domain.camera.domain.usecases.GetUploadVideoUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.channels.channeldetails.domain.usecase.GetChannelDataUseCase
import com.rumble.domain.common.domain.usecase.IsDevelopModeUseCase
import com.rumble.domain.profile.domain.GetAppVersionUseCase
import com.rumble.domain.profile.domain.SignOutUseCase
import com.rumble.domain.profile.domainmodel.AppVersionEntity
import com.rumble.domain.profile.domainmodel.AppVersionVisibility
import com.rumble.domain.settings.domain.domainmodel.ColorMode
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.domain.uploadmanager.dto.VideoUploadsIndicatorStatus
import com.rumble.network.session.SessionManager
import com.rumble.utils.RumbleConstants.PROFILE_IMAGE_CLICK_TIMES
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ProfileHandler {
    fun onSignOut()
    fun onSignOutConfirmed()
    fun onDismissDialog()
    fun onProfileImageClick()
    fun onVersionClick()

    val uiState: State<ProfileScreenUiState>
    val screenSate: State<ProfileScreenState>
    val colorMode: Flow<ColorMode>
    val alertDialogState: StateFlow<AlertDialogState>
    val appVersionState: StateFlow<AppVersionEntity>
    val vmEvents: Flow<ProfileScreenEvent>
}

data class ProfileScreenUiState(
    val userName: String = "",
    val userPicture: String = "",
    val userActiveUploadsProgress: Float = 0F,
    val userActiveUploadsIndicatorStatus: VideoUploadsIndicatorStatus = VideoUploadsIndicatorStatus.None,
    val developMode: Boolean = false,
    val isPremiumUser: Boolean = false,
    val isLoggedIn: Boolean = true,
)

sealed class ProfileScreenState {
    data class LoggedIn(val channelDetailsEntity: CreatorEntity?) : ProfileScreenState()
    data object Loading : ProfileScreenState()
    data object LoggedOut : ProfileScreenState()
}

sealed class ProfileAlertDialogReason : AlertDialogReason {
    data object ConfirmSignOut : ProfileAlertDialogReason()
}

sealed class ProfileScreenEvent {
    data object Error : ProfileScreenEvent()
    data class CopyVersionToClipboard(val version: String) : ProfileScreenEvent()
    data object NavigateHome: ProfileScreenEvent()
}

private const val TAG = "ProfileViewModel"

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    userPreferenceManager: UserPreferenceManager,
    private val signOutUseCase: SignOutUseCase,
    private val googleSignInClient: GoogleSignInClient,
    getChannelDataUseCase: GetChannelDataUseCase,
    getAppVersionUseCase: GetAppVersionUseCase,
    private val getUploadVideoUseCase: GetUploadVideoUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    isDevelopModeUseCase: IsDevelopModeUseCase,
) : ViewModel(), ProfileHandler {

    override val uiState: MutableState<ProfileScreenUiState> =
        mutableStateOf(ProfileScreenUiState(developMode = isDevelopModeUseCase()))
    override val screenSate: MutableState<ProfileScreenState> =
        mutableStateOf(ProfileScreenState.LoggedIn(null))
    override val colorMode: Flow<ColorMode> = userPreferenceManager.colorMode
    override val alertDialogState = MutableStateFlow(AlertDialogState())
    override val appVersionState = MutableStateFlow(getAppVersionUseCase())

    private val _vmEvents = Channel<ProfileScreenEvent>(capacity = Channel.CONFLATED)
    override val vmEvents: Flow<ProfileScreenEvent> = _vmEvents.receiveAsFlow()

    private var profileImageClickCount: Int = 0

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        handleError(throwable)
    }

    init {
        viewModelScope.launch(errorHandler) {
            sessionManager.cookiesFlow.distinctUntilChanged().collect {
                uiState.value = uiState.value.copy(isLoggedIn = it.isNotEmpty())
                if (it.isNotEmpty()) {
                    val userId = sessionManager.userIdFlow.first()
                    getChannelDataUseCase(userId)
                        .onSuccess { channelDetailEntity ->
                            screenSate.value = ProfileScreenState.LoggedIn(channelDetailEntity)
                        }
                        .onFailure { throwable ->
                            handleError(throwable)
                        }
                }
            }
        }
        observeUserSate()
        observeUserActiveUploads()
    }

    override fun onSignOut() {
        alertDialogState.value = AlertDialogState(
            show = true,
            alertDialogReason = ProfileAlertDialogReason.ConfirmSignOut
        )
    }

    override fun onSignOutConfirmed() {
        alertDialogState.value = AlertDialogState()
        viewModelScope.launch(errorHandler) {
            screenSate.value = ProfileScreenState.Loading
            signOutUseCase {
                LoginManager.getInstance().logOut()
                googleSignInClient.signOut()
                screenSate.value = ProfileScreenState.LoggedOut
            }
            emitVmEvent(ProfileScreenEvent.NavigateHome)
        }
    }

    override fun onDismissDialog() {
        alertDialogState.value = AlertDialogState()
    }

    override fun onProfileImageClick() {
        if (++profileImageClickCount >= PROFILE_IMAGE_CLICK_TIMES) {
            profileImageClickCount = 0
            appVersionState.update { it.copy(visibility = AppVersionVisibility.Visible) }
        }
    }

    override fun onVersionClick() {
        emitVmEvent(
            ProfileScreenEvent.CopyVersionToClipboard(
                version = appVersionState.value.versionString
            )
        )
    }

    private fun observeUserSate() {
        viewModelScope.launch {
            sessionManager.userNameFlow.distinctUntilChanged().collect {
                uiState.value = uiState.value.copy(userName = it)
            }
        }
        viewModelScope.launch {
            sessionManager.userPictureFlow.distinctUntilChanged().collect {
                uiState.value = uiState.value.copy(userPicture = it)
            }
        }
        viewModelScope.launch {
            sessionManager.isPremiumUserFlow.distinctUntilChanged().collect {
                uiState.value = uiState.value.copy(isPremiumUser = it)
            }
        }
    }

    private fun observeUserActiveUploads() {
        viewModelScope.launch {
            getUploadVideoUseCase().collect { uploadList ->
                val activeUploads = uploadList.filterNot {
                    it.status == UploadStatus.DRAFT
                }
                if (activeUploads.isNotEmpty()) {
                    val status = mapActiveUploadsStatus(activeUploads)
                    if (status == VideoUploadsIndicatorStatus.Processing) {
                        val progress = calculateUploadProgress(activeUploads)
                        uiState.value = uiState.value.copy(
                            userActiveUploadsProgress = progress,
                            userActiveUploadsIndicatorStatus = status,
                        )
                    } else {
                        uiState.value = uiState.value.copy(
                            userActiveUploadsIndicatorStatus = status
                        )
                    }
                } else {
                    uiState.value = uiState.value.copy(
                        userActiveUploadsProgress = 0F,
                        userActiveUploadsIndicatorStatus = VideoUploadsIndicatorStatus.None
                    )
                }
            }
        }
    }

    private fun mapActiveUploadsStatus(activeUploads: List<UploadVideoEntity>): VideoUploadsIndicatorStatus {
        var status = VideoUploadsIndicatorStatus.Finished
        activeUploads.forEach {
            if (it.status == UploadStatus.UPLOADING_FAILED
                || it.status == UploadStatus.EMAIL_VERIFICATION_NEEDED
                || it.status == UploadStatus.WAITING_CONNECTION
                || it.status == UploadStatus.WAITING_WIFI
            ) {
                status = VideoUploadsIndicatorStatus.Error
                return@forEach
            } else if (it.status == UploadStatus.PROCESSING
                || it.status == UploadStatus.UPLOADING
                || it.status == UploadStatus.FINALIZING
            ) {
                status = VideoUploadsIndicatorStatus.Processing
                return@forEach
            }
        }
        return status
    }

    private fun calculateUploadProgress(activeUploads: List<UploadVideoEntity>): Float {
        var totalProgress = 0F
        activeUploads.forEach {
            totalProgress += it.progress
        }
        return totalProgress.div(activeUploads.size)
    }

    private fun handleError(throwable: Throwable) {
        unhandledErrorUseCase(TAG, throwable)
        emitVmEvent(ProfileScreenEvent.Error)
    }

    private fun emitVmEvent(vmEvent: ProfileScreenEvent) {
        _vmEvents.trySend(vmEvent)
    }
}