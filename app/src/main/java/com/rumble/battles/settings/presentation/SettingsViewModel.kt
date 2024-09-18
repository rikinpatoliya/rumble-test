package com.rumble.battles.settings.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.battles.BuildConfig
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.dialogs.AlertDialogState
import com.rumble.battles.navigation.RumblePath
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.camera.UploadStatus
import com.rumble.domain.camera.domain.usecases.GetUploadVideoUseCase
import com.rumble.domain.camera.domain.usecases.RestartUploadVideoUseCase
import com.rumble.domain.common.domain.usecase.IsDevelopModeUseCase
import com.rumble.domain.login.domain.domainmodel.LoginType
import com.rumble.domain.settings.domain.domainmodel.AuthProviderEntity
import com.rumble.domain.settings.domain.domainmodel.BackgroundPlay
import com.rumble.domain.settings.domain.domainmodel.CanSubmitLogsResult
import com.rumble.domain.settings.domain.domainmodel.ColorMode
import com.rumble.domain.settings.domain.domainmodel.PlaybackInFeedsMode
import com.rumble.domain.settings.domain.domainmodel.UploadQuality
import com.rumble.domain.settings.domain.usecase.GetAuthProvidersUseCase
import com.rumble.domain.settings.domain.usecase.GetCanSubmitLogsUseCase
import com.rumble.domain.settings.domain.usecase.GetNotificationSettingsUseCase
import com.rumble.domain.settings.domain.usecase.SendFeedbackUseCase
import com.rumble.domain.settings.domain.usecase.ShareLogsUseCase
import com.rumble.domain.settings.domain.usecase.UnlinkAuthProviderUseCase
import com.rumble.domain.settings.domain.usecase.UpdateSubdomainUseCase
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.network.session.SessionManager
import com.rumble.network.subdomain.RumbleSubdomain
import com.rumble.network.subdomain.RumbleSubdomainUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

interface SettingsHandler {
    val uiState: StateFlow<SettingsScreenUIState>
    val loginTypeFlow: Flow<LoginType>
    val backgroundPlay: Flow<BackgroundPlay>
    val uploadOverWifi: Flow<Boolean>
    val uploadQuality: Flow<UploadQuality>
    val vmEvents: Flow<SettingsScreenVmEvent>
    val playbackInFeedsMode: Flow<PlaybackInFeedsMode>
    val disableAdsFlow: Flow<Boolean>
    val forceAdsFlow: Flow<Boolean>
    val playDebugAdFlow: Flow<Boolean>

    fun onUpdateBackgroundPlay(backgroundPlay: BackgroundPlay)
    fun onUpdatePlaybackInFeed(playbackInFeedsMode: PlaybackInFeedsMode)
    fun onUpdateUploadOverWifi(value: Boolean)
    fun onUpdateUploadQuality(uploadQuality: UploadQuality)
    fun onResetSubdomain()
    fun onAskUnlinkConfirmation(loginType: LoginType)
    fun onUnlinkAccount(loginType: LoginType)
    fun onDismissDialog()
    fun onVersionClick()
    fun onShareLogs(title: String, widthDp: Int, heightDp: Int, width: Int, height: Int)
    fun onDisableAds(disableAds: Boolean)
    fun onForceAds(forceAds: Boolean)
    fun onPlayDebugAd(playAd: Boolean)
    fun onSendFeedback()
    fun onChangeColorMode(colorMode: ColorMode)
    fun onAutoplayOn(on: Boolean)
}

sealed class SettingsAlertDialogReason : AlertDialogReason {
    data class UnlinkConfirmationDialog(val loginType: LoginType) : SettingsAlertDialogReason()
    object ChangeSubdomainConfirmationDialog : SettingsAlertDialogReason()
}

data class SettingsScreenUIState(
    val debugState: DebugUIState,
    val alertDialogState: AlertDialogState = AlertDialogState(),
    val appVersion: String = "${BuildConfig.VERSION_NAME}.${BuildConfig.VERSION_CODE}",
    val loading: Boolean = false,
    val forceAds: Boolean = false,
    val playDebugAd: Boolean = false,
    val colorMode: ColorMode = ColorMode.SYSTEM_DEFAULT,
    val autoplayEnabled: Boolean = false,
    val userLoggedIn: Boolean = false,
)

data class DebugUIState(
    val rumbleSubdomain: RumbleSubdomain = RumbleSubdomain(),
    val canUseSubdomain: Boolean = false,
    val authProviderEntity: AuthProviderEntity? = null,
    val canSubmitLogs: Boolean = false,
    val canUseAdsDebugMode: Boolean = false,
)

sealed class SettingsScreenVmEvent {
    data class Error(val errorMessage: String? = null) : SettingsScreenVmEvent()
    data class AccountUnlinkSuccess(val loginType: LoginType) : SettingsScreenVmEvent()
    object ScrollToPlaybackSettings : SettingsScreenVmEvent()
    data class CopyVersionToClipboard(val version: String) : SettingsScreenVmEvent()
}

private const val TAG = "SettingsViewModel"

@HiltViewModel
class SettingsViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val sessionManager: SessionManager,
    private val getNotificationSettingsUseCase: GetNotificationSettingsUseCase,
    private val rumbleSubdomainUseCase: RumbleSubdomainUseCase,
    private val updateSubdomainUseCase: UpdateSubdomainUseCase,
    private val getAuthProvidersUseCase: GetAuthProvidersUseCase,
    private val getCanSubmitLogsUseCase: GetCanSubmitLogsUseCase,
    private val shareLogsUseCase: ShareLogsUseCase,
    private val unlinkAuthProviderUseCase: UnlinkAuthProviderUseCase,
    private val userPreferenceManager: UserPreferenceManager,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val getUploadVideoUseCase: GetUploadVideoUseCase,
    private val restartUploadVideoUseCase: RestartUploadVideoUseCase,
    private val isDevelopModeUseCase: IsDevelopModeUseCase,
    private val sendFeedbackUseCase: SendFeedbackUseCase,
) : ViewModel(), SettingsHandler {

    private val scrollToPlayback = savedState.get<Boolean>(RumblePath.PARAMETER.path) ?: false

    override val uiState =
        MutableStateFlow(
            SettingsScreenUIState(
                debugState = DebugUIState(
                    canUseSubdomain = isDevelopModeUseCase(),
                    canUseAdsDebugMode = isDevelopModeUseCase()
                )
            )
        )
    override val loginTypeFlow: Flow<LoginType> =
        sessionManager.loginTypeFlow.map { LoginType.getByValue(it) }
    override val backgroundPlay: Flow<BackgroundPlay> = userPreferenceManager.backgroundPlayFlow
    override val uploadOverWifi: Flow<Boolean> = userPreferenceManager.uploadOverWifiFLow
    override val uploadQuality: Flow<UploadQuality> = userPreferenceManager.uploadQualityFlow
    override val playbackInFeedsMode: Flow<PlaybackInFeedsMode> =
        userPreferenceManager.playbackInFeedsModeModeFlow
    override val disableAdsFlow: Flow<Boolean> = userPreferenceManager.disableAdsFlow
    override val forceAdsFlow: Flow<Boolean> = userPreferenceManager.forceAdsFlow
    override val playDebugAdFlow: Flow<Boolean> = userPreferenceManager.playDebugAdFlow

    private val _vmEvents = Channel<SettingsScreenVmEvent>(capacity = Channel.CONFLATED)
    override val vmEvents: Flow<SettingsScreenVmEvent> = _vmEvents.receiveAsFlow()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
        uiState.update { it.copy(loading = false) }
        emitVmEvent(SettingsScreenVmEvent.Error())
    }

    init {
        observeCookies()
        observeColorMode()
        observeAutoplayMode()
    }

    override fun onUpdateBackgroundPlay(backgroundPlay: BackgroundPlay) {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferenceManager.saveBackgroundPlay(backgroundPlay)
        }
    }

    override fun onUpdateUploadOverWifi(value: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferenceManager.saveUploadOverWifi(value)
            getUploadVideoUseCase().first().forEach { uploadVideoEntity ->
                when (uploadVideoEntity.status) {
                    UploadStatus.PROCESSING,
                    UploadStatus.WAITING_CONNECTION,
                    UploadStatus.WAITING_WIFI,
                    UploadStatus.UPLOADING -> restartUploadVideoUseCase(
                        uploadVideoEntity.uuid
                    )

                    else -> {}//No need to do anything
                }
            }
        }
    }

    override fun onUpdateUploadQuality(uploadQuality: UploadQuality) {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferenceManager.saveUploadQuality(uploadQuality)
        }
    }

    override fun onResetSubdomain() {
        viewModelScope.launch(Dispatchers.IO) {
            uiState.update {
                it.copy(loading = true)
            }
            updateSubdomainUseCase(uiState.value.debugState.rumbleSubdomain.environmentSubdomain)
            uiState.update {
                it.copy(
                    alertDialogState = AlertDialogState(
                        show = true,
                        alertDialogReason = SettingsAlertDialogReason.ChangeSubdomainConfirmationDialog
                    ),
                    loading = false
                )
            }
        }
    }

    override fun onAskUnlinkConfirmation(loginType: LoginType) {
        uiState.update {
            it.copy(
                alertDialogState = AlertDialogState(
                    show = true,
                    alertDialogReason = SettingsAlertDialogReason.UnlinkConfirmationDialog(loginType)
                ),
                loading = false
            )
        }
    }

    override fun onUnlinkAccount(loginType: LoginType) {
        viewModelScope.launch(errorHandler) {
            onDismissDialog()
            uiState.update {
                it.copy(loading = true)
            }
            if (unlinkAuthProviderUseCase(loginType)) {
                uiState.update {
                    it.copy(
                        loading = false,
                        debugState = if (it.debugState.canUseSubdomain)
                            DebugUIState(canUseSubdomain = true, authProviderEntity = null)
                        else
                            DebugUIState(canUseAdsDebugMode = isDevelopModeUseCase())
                    )
                }
                uiState.update {
                    it.copy(loading = false)
                }
                emitVmEvent(SettingsScreenVmEvent.AccountUnlinkSuccess(loginType))
            } else {
                uiState.update {
                    it.copy(loading = false)
                }
                emitVmEvent(SettingsScreenVmEvent.Error())
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

    override fun onVersionClick() {
        emitVmEvent(event = SettingsScreenVmEvent.CopyVersionToClipboard(uiState.value.appVersion))
    }

    override fun onShareLogs(title: String, widthDp: Int, heightDp: Int, width: Int, height: Int) {
        Timber.i(
            "\nAppId: %s,\n" +
                "Version: %s:%s\n" +
                "Device model: %s\n" +
                "Device manufacturer: %s\n" +
                "Device brand: %s\n" +
                "Device display name: %s\n" +
                "Device product name: %s\n" +
                "Device API: %s\n" +
                "Device Android Version: %s\n" +
                "Screen size dp: %s,%s\n" +
                "Screen size px: %s,%s ",
            BuildConfig.APPLICATION_ID,
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE,
            android.os.Build.MODEL,
            android.os.Build.MANUFACTURER,
            android.os.Build.BRAND,
            android.os.Build.DISPLAY,
            android.os.Build.PRODUCT,
            android.os.Build.VERSION.SDK_INT,
            android.os.Build.VERSION.RELEASE,
            widthDp,
            heightDp,
            width,
            height,
        )

        if (!shareLogsUseCase()) {
            emitVmEvent(SettingsScreenVmEvent.Error())
        }
    }

    override fun onUpdatePlaybackInFeed(playbackInFeedsMode: PlaybackInFeedsMode) {
        viewModelScope.launch {
            userPreferenceManager.savePlaybackInFeedsMode(playbackInFeedsMode)
        }
    }

    override fun onDisableAds(disableAds: Boolean) {
        viewModelScope.launch {
            userPreferenceManager.saveDisableAdsMode(disableAds)
        }
    }

    override fun onForceAds(forceAds: Boolean) {
        viewModelScope.launch {
            userPreferenceManager.saveForceAds(forceAds)
        }
    }

    override fun onPlayDebugAd(playAd: Boolean) {
        viewModelScope.launch {
            userPreferenceManager.savePlayDebugAd(playAd)
        }
    }

    override fun onSendFeedback() {
        viewModelScope.launch(errorHandler) {
            sendFeedbackUseCase()
        }
    }

    override fun onChangeColorMode(colorMode: ColorMode) {
        viewModelScope.launch {
            userPreferenceManager.saveColorMode(colorMode)
        }
    }

    override fun onAutoplayOn(on: Boolean) {
        viewModelScope.launch {
            userPreferenceManager.saveAutoplayOn(on)
        }
    }

    private fun observeCookies() {
        viewModelScope.launch {
            sessionManager.cookiesFlow.distinctUntilChanged().collectLatest { cookie ->
                uiState.update { it.copy(userLoggedIn = cookie.isNotEmpty()) }
                fetchInitialSettings(cookie)
            }
        }
    }

    private fun observeColorMode() {
        viewModelScope.launch {
            userPreferenceManager.colorMode.distinctUntilChanged().collectLatest { mode ->
                uiState.update { it.copy(colorMode = mode) }
            }
        }
    }

    private fun observeAutoplayMode() {
        viewModelScope.launch {
            userPreferenceManager.autoplayFlow.distinctUntilChanged().collectLatest { enabled ->
                uiState.update { it.copy(autoplayEnabled = enabled) }
            }
        }
    }

    private fun emitVmEvent(event: SettingsScreenVmEvent) {
        _vmEvents.trySend(event)
    }

    private suspend fun fetchInitialSettings(cookie: String) {
        viewModelScope.launch(errorHandler) {
            uiState.update { it.copy(loading = true) }
            if (cookie.isNotEmpty()) {
                val notificationSettingsResult = getNotificationSettingsUseCase()
                if (notificationSettingsResult.success) {
                    uiState.update {
                        it.copy(
                            debugState = it.debugState.copy(
                                canUseSubdomain = notificationSettingsResult.canUseCustomApiDomain,
                                canUseAdsDebugMode = notificationSettingsResult.canUseCustomApiDomain || isDevelopModeUseCase()
                            ),
                            loading = false,
                        )
                    }
                }
                val getAuthProvidersResult = getAuthProvidersUseCase()
                if (getAuthProvidersResult.success) {
                    uiState.update {
                        it.copy(
                            debugState = it.debugState.copy(
                                authProviderEntity = getAuthProvidersResult.authProviderEntity
                            )
                        )
                    }
                }
            }
            val subdomain = async {
                val rumbleSubdomainResult = rumbleSubdomainUseCase()
                uiState.update {
                    it.copy(
                        debugState = it.debugState.copy(rumbleSubdomain = rumbleSubdomainResult)
                    )
                }
            }

            val canSubmitLogs = async {
                val canSubmitLogsResult = getCanSubmitLogsUseCase()
                if (canSubmitLogsResult is CanSubmitLogsResult.Success)
                    uiState.update {
                        it.copy(
                            debugState = it.debugState.copy(canSubmitLogs = canSubmitLogsResult.canSubmitLogs)
                        )
                    }
            }

            try {
                subdomain.await()
                canSubmitLogs.await()
            } catch (e: Exception) {
                unhandledErrorUseCase(TAG, e)
            } finally {
                uiState.update {
                    it.copy(
                        loading = false
                    )
                }
                if (scrollToPlayback) emitVmEvent(SettingsScreenVmEvent.ScrollToPlaybackSettings)
            }
        }
    }
}