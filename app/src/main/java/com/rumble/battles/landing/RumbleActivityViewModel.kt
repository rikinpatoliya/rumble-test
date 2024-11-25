package com.rumble.battles.landing

import android.app.Application
import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.rumble.analytics.PushNotificationHandlingFailedEvent
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.dialogs.AlertDialogState
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.common.domain.usecase.AnnotatedStringUseCase
import com.rumble.domain.common.domain.usecase.AnnotatedStringWithActionsList
import com.rumble.domain.common.domain.usecase.IsDevelopModeUseCase
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.landing.usecases.GetUserCookiesUseCase
import com.rumble.domain.landing.usecases.LoginRequiredUseCase
import com.rumble.domain.landing.usecases.PipIsAvailableUseCase
import com.rumble.domain.landing.usecases.SaveVersionCodeUseCase
import com.rumble.domain.landing.usecases.SilentLoginUseCase
import com.rumble.domain.landing.usecases.TransferUserDataUseCase
import com.rumble.domain.landing.usecases.UpdateMediaSessionUseCase
import com.rumble.domain.logging.domain.usecase.InitProductionLoggingUseCase
import com.rumble.domain.login.domain.usecases.GetClearSessionOnAppStartUseCase
import com.rumble.domain.notifications.domain.domainmodel.KEY_NOTIFICATION_VIDEO_DETAILS
import com.rumble.domain.notifications.domain.domainmodel.NotificationHandlerResult
import com.rumble.domain.notifications.domain.domainmodel.RumbleNotificationData
import com.rumble.domain.notifications.domain.usecases.RumbleNotificationHandlerUseCase
import com.rumble.domain.profile.domain.GetUserHasUnreadNotificationsUseCase
import com.rumble.domain.profile.domain.SignOutUseCase
import com.rumble.domain.settings.domain.domainmodel.BackgroundPlay
import com.rumble.domain.settings.domain.domainmodel.ColorMode
import com.rumble.domain.settings.domain.usecase.PrepareAppForTestingUseCase
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.domain.video.domain.usecases.GenerateViewerIdUseCase
import com.rumble.network.session.SessionManager
import com.rumble.utils.RumbleConstants.SCREEN_OFF_DELAY
import com.rumble.utils.RumbleConstants.TESTING_LAUNCH_UIT_FLAG
import com.rumble.utils.RumbleConstants.TESTING_LAUNCH_UIT_PASSWORD
import com.rumble.utils.RumbleConstants.TESTING_LAUNCH_UIT_USERNAME
import com.rumble.utils.extension.isScreenOn
import com.rumble.videoplayer.player.PlayerTargetChangeListener
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.player.config.PlayerTarget
import com.rumble.videoplayer.player.config.RumbleVideoMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.UUID
import javax.inject.Inject

private const val TAG = "RumbleActivityViewModel"

interface RumbleActivityHandler {
    val eventFlow: Flow<RumbleEvent>
    val alertDialogState: State<AlertDialogState>
    val activityHandlerState: StateFlow<ActivityHandlerState>
    var currentPlayer: RumblePlayer?
    val colorMode: Flow<ColorMode>
    val isLaunchedFromNotification: State<Boolean>

    fun onToggleAppLaunchedFromNotification(fromNotification: Boolean)
    suspend fun backgroundSoundIsAvailable(): Boolean
    suspend fun getCookies(): String
    fun startObserveCookies()
    fun initLogging()
    fun initMediaSession(session: MediaSessionCompat)
    fun onError(e: Throwable)
    fun onAppPaused()
    fun onAppResumed()
    fun onUserLeaveHint(lifecycleStateAtLeastStarted: Boolean)
    fun onEnterPipMode()
    fun onExitPipMode()
    fun clearNotifications()
    fun loadNotificationState()
    fun disableDynamicOrientationChangeBasedOnDeviceType()
    fun onShowAlertDialog(reason: RumbleActivityAlertReason)
    fun onDismissDialog()
    fun onPauseVideo()
    fun onPremiumPurchased()
    fun onOpenWebView(url: String)
    fun closeApp()
    fun showSnackbar(
        message: String,
        title: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Long
    )

    fun onAnnotatedTextClicked(
        annotatedTextWithActions: AnnotatedStringWithActionsList,
        offset: Int
    )

    fun onNavigateToMyVideos()
    fun onLogException(e: Exception)
    fun handleNotifications(bundle: Bundle?)
    fun onDisplayRepostUndoWarning(repostId: Long)
}


sealed class RumbleEvent {
    data class NavigateToVideoDetailsFromNotification(val videoEntity: VideoEntity) : RumbleEvent()
    data object NavigateToMyVideos : RumbleEvent()
    data object UnexpectedError : RumbleEvent()
    data object PipModeEntered : RumbleEvent()
    data object DisableDynamicOrientationChangeBasedOnDeviceType : RumbleEvent()
    data object CloseApp : RumbleEvent()
    data object PremiumPurchased : RumbleEvent()
    data object EnterPipMode : RumbleEvent()
    data class OpenWebView(val url: String) : RumbleEvent()
    data class ShowSnackbar(
        val message: String,
        val title: String? = null,
        val duration: SnackbarDuration = SnackbarDuration.Long
    ) : RumbleEvent()

    data object NavigateToAuth : RumbleEvent()
}

sealed class RumbleActivityAlertReason : AlertDialogReason {
    data object VideoDetailsFromNotificationFailedReason : RumbleActivityAlertReason()
    data object DeleteWatchHistoryConfirmationReason : RumbleActivityAlertReason()
    data class DeletePlayListConfirmationReason(val playListId: String) :
        RumbleActivityAlertReason()

    data class UnfollowConfirmationReason(val channel: CreatorEntity) :
        RumbleActivityAlertReason()

    data object PremiumPurchaseMade : RumbleActivityAlertReason()
    data object SubscriptionNotAvailable : RumbleActivityAlertReason()
    data class UndoRepostWarning(val repostId: Long) : RumbleActivityAlertReason()
}

data class ActivityHandlerState(
    val hasUnreadNotifications: Boolean = false,
    val readyToStart: Boolean = false,
    val needLogin: Boolean = false,
)

@Suppress("DEPRECATION")
@HiltViewModel
class RumbleActivityViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userPreferenceManager: UserPreferenceManager,
    generateViewerIdUseCase: GenerateViewerIdUseCase,
    private val getUserCookiesUseCase: GetUserCookiesUseCase,
    private val transferUserDataUseCase: TransferUserDataUseCase,
    private val silentLoginUseCase: SilentLoginUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val sessionManager: SessionManager,
    private val rumbleNotificationHandlerUseCase: RumbleNotificationHandlerUseCase,
    private val analyticsEventUseCase: AnalyticsEventUseCase,
    private val pipIsAvailableUseCase: PipIsAvailableUseCase,
    private val updateMediaSessionUseCase: UpdateMediaSessionUseCase,
    private val initProductionLoggingUseCase: InitProductionLoggingUseCase,
    private val getUserHasUnreadNotificationsUseCase: GetUserHasUnreadNotificationsUseCase,
    private val getClearSessionOnAppStartUseCase: GetClearSessionOnAppStartUseCase,
    private val annotatedStringUseCase: AnnotatedStringUseCase,
    private val loginRequiredUseCase: LoginRequiredUseCase,
    private val saveVersionCodeUseCase: SaveVersionCodeUseCase,
    private val prepareAppForTestingUseCase: PrepareAppForTestingUseCase,
    private val developModeUseCase: IsDevelopModeUseCase,
    application: Application,
) : AndroidViewModel(application), RumbleActivityHandler, PlayerTargetChangeListener {

    override val isLaunchedFromNotification: MutableState<Boolean> = mutableStateOf(false)
    override val colorMode: Flow<ColorMode> = userPreferenceManager.colorMode
    override val eventFlow: MutableSharedFlow<RumbleEvent> = MutableSharedFlow()
    override val alertDialogState: MutableState<AlertDialogState> =
        mutableStateOf(AlertDialogState())
    override val activityHandlerState: MutableStateFlow<ActivityHandlerState> =
        MutableStateFlow(ActivityHandlerState())
    override var currentPlayer: RumblePlayer? = null
        set(value) {
            field = value
            field?.targetChangeListener = this
        }

    private var mediaSession: MediaSessionCompat? = null
    private var notificationGuid: String = ""
    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
    }

    init {
        initState(savedStateHandle)
        viewModelScope.launch {
            if (getClearSessionOnAppStartUseCase()) {
                signOutUseCase()
            }
        }
        viewModelScope.launch(errorHandler) {
            generateViewerIdUseCase()
            sessionManager.saveUniqueSession(UUID.randomUUID().toString())
        }
        loadNotificationState()
        saveAppVersion()
    }

    override fun onLogException(e: Exception) {
        unhandledErrorUseCase(TAG, e)
    }

    override fun onPlayerTargetChanged(currentTarget: PlayerTarget) {
        mediaSession?.let {
            updateMediaSessionUseCase(it, currentPlayer, currentTarget != PlayerTarget.AD)
        }
    }

    override fun onToggleAppLaunchedFromNotification(fromNotification: Boolean) {
        isLaunchedFromNotification.value = fromNotification
    }

    override suspend fun getCookies(): String {
        try {
            transferUserDataUseCase()
        } catch (e: Exception) {
            unhandledErrorUseCase(TAG, e)
        }
        return getUserCookiesUseCase().ifEmpty {
            silentLoginUseCase()
            getUserCookiesUseCase()
        }
    }

    private suspend fun pipIsAvailable(): Boolean =
        pipIsAvailableUseCase() && currentPlayer != null

    private fun getVideoDetails(rumbleNotificationData: RumbleNotificationData) {
        viewModelScope.launch(errorHandler) {
            when (val result = rumbleNotificationHandlerUseCase(rumbleNotificationData)) {
                NotificationHandlerResult.UnhandledNotificationData -> {
                    analyticsEventUseCase(PushNotificationHandlingFailedEvent)
                    unhandledErrorUseCase(
                        "UnhandledNotificationData",
                        Throwable(rumbleNotificationData.toString())
                    )
                    onShowAlertDialog(RumbleActivityAlertReason.VideoDetailsFromNotificationFailedReason)
                    sessionManager.allowContentLoadFlow(true)
                }

                is NotificationHandlerResult.VideoDetailsNotificationData -> {
                    if (result.success && result.videoEntity != null) {
                        if (rumbleNotificationData.guid != notificationGuid) {
                            notificationGuid = rumbleNotificationData.guid
                            result.videoEntity?.let {
                                emitVmEvent(RumbleEvent.NavigateToVideoDetailsFromNotification(it))
                            }
                        }
                    } else {
                        unhandledErrorUseCase(
                            "VideoDetailsNotificationData",
                            Throwable(rumbleNotificationData.toString())
                        )
                        onShowAlertDialog(RumbleActivityAlertReason.VideoDetailsFromNotificationFailedReason)
                        sessionManager.allowContentLoadFlow(true)
                    }
                }
            }
        }
    }

    override fun startObserveCookies() {
        viewModelScope.launch {
            sessionManager.cookiesFlow.distinctUntilChanged().collect {
                if (it.isEmpty()) {
                    if (silentLoginUseCase().not()) signOutUseCase()
                }
            }
        }
    }

    override fun initLogging() {
        viewModelScope.launch {
            sessionManager.canSubmitLogs.distinctUntilChanged().collect {
                initProductionLoggingUseCase(canSubmitLogs = it)
            }
        }
    }

    override fun initMediaSession(session: MediaSessionCompat) {
        mediaSession = session
    }

    override suspend fun backgroundSoundIsAvailable(): Boolean =
        userPreferenceManager.backgroundPlayFlow.first() == BackgroundPlay.SOUND

    override fun onError(e: Throwable) {
        unhandledErrorUseCase(TAG, e)
        emitVmEvent(RumbleEvent.UnexpectedError)
    }

    override fun onAppPaused() {
        runBlocking {
            delay(SCREEN_OFF_DELAY)
            val screenOff = getApplication<Application>().isScreenOn().not()
            if (backgroundSoundIsAvailable().not() && screenOff) {
                currentPlayer?.pauseVideo()
                currentPlayer?.setRumbleVideoMode(RumbleVideoMode.BackgroundPaused)
            } else {
                currentPlayer?.setRumbleVideoMode(RumbleVideoMode.BackgroundSoundOnly)
            }
        }
    }

    override fun onAppResumed() {
        val rumbleVideoMode = currentPlayer?.rumbleVideoMode?.value
        if (rumbleVideoMode == RumbleVideoMode.BackgroundSoundOnly ||
            rumbleVideoMode == RumbleVideoMode.BackgroundPaused
        ) {
            currentPlayer?.setRumbleVideoMode(RumbleVideoMode.Normal)
        }
    }

    override fun onUserLeaveHint(lifecycleStateAtLeastStarted: Boolean) {
        viewModelScope.launch {
            if (sessionManager.disablePipFlow.first().not()) {
                val showUpNext = currentPlayer?.showUpNext?.value ?: false

                if (showUpNext) {
                    // Autoplay UI is shown
                    currentPlayer?.setRumbleVideoMode(RumbleVideoMode.BackgroundPaused)
                } else {
                    if (pipIsAvailable() && lifecycleStateAtLeastStarted) {
                        emitVmEvent(RumbleEvent.EnterPipMode)
                    } else if (backgroundSoundIsAvailable().not()) {
                        currentPlayer?.pauseVideo()
                        currentPlayer?.setRumbleVideoMode(RumbleVideoMode.BackgroundPaused)
                    } else {
                        currentPlayer?.setRumbleVideoMode(RumbleVideoMode.BackgroundSoundOnly)
                    }
                }
            } else {
                sessionManager.saveDisablePip(false)
            }
        }
    }

    override fun onEnterPipMode() {
        mediaSession?.let {
            updateMediaSessionUseCase(
                it,
                currentPlayer,
                currentPlayer?.playerTarget?.value != PlayerTarget.AD
            )
        }
        currentPlayer?.hideControls()
        currentPlayer?.setRumbleVideoMode(RumbleVideoMode.Pip)
        emitVmEvent(RumbleEvent.PipModeEntered)
    }

    override fun onExitPipMode() {
        currentPlayer?.setRumbleVideoMode(RumbleVideoMode.Normal)
    }

    private fun emitVmEvent(event: RumbleEvent) =
        viewModelScope.launch { eventFlow.emit(event) }

    override fun loadNotificationState() {
        viewModelScope.launch(errorHandler) {
            if (sessionManager.isUserSignedIn()) {
                val hasNotifications = getUserHasUnreadNotificationsUseCase()
                activityHandlerState.value =
                    activityHandlerState.value.copy(hasUnreadNotifications = hasNotifications)
            }
        }
    }

    override fun clearNotifications() {
        activityHandlerState.value =
            activityHandlerState.value.copy(hasUnreadNotifications = false)
    }

    override fun disableDynamicOrientationChangeBasedOnDeviceType() {
        emitVmEvent(RumbleEvent.DisableDynamicOrientationChangeBasedOnDeviceType)
    }

    override fun onShowAlertDialog(reason: RumbleActivityAlertReason) {
        alertDialogState.value = AlertDialogState(
            true,
            reason
        )
    }

    override fun onDismissDialog() {
        alertDialogState.value = AlertDialogState()
    }

    override fun onPauseVideo() {
        currentPlayer?.pauseVideo()
    }

    override fun onPremiumPurchased() {
        emitVmEvent(RumbleEvent.PremiumPurchased)
    }

    override fun onOpenWebView(url: String) {
        emitVmEvent(RumbleEvent.OpenWebView(url))
    }

    override fun closeApp() {
        emitVmEvent(RumbleEvent.CloseApp)
    }

    override fun showSnackbar(message: String, title: String?, duration: SnackbarDuration) {
        emitVmEvent(RumbleEvent.ShowSnackbar(message, title, duration))
    }

    override fun onAnnotatedTextClicked(
        annotatedTextWithActions: AnnotatedStringWithActionsList,
        offset: Int
    ) {
        annotatedStringUseCase.invoke(annotatedTextWithActions, offset)
    }

    override fun onNavigateToMyVideos() {
        emitVmEvent(RumbleEvent.NavigateToMyVideos)
    }

    private fun handleLaunchAttributesForTesting(savedStateHandle: SavedStateHandle): Boolean {
        return if (developModeUseCase()) {
            val uitFlag: Any? = savedStateHandle[TESTING_LAUNCH_UIT_FLAG]
            if (uitFlag != null) {
                val uitUserName: String? = savedStateHandle[TESTING_LAUNCH_UIT_USERNAME]
                val uitPassword: String? = savedStateHandle[TESTING_LAUNCH_UIT_PASSWORD]
                onPrepareAppForTesting(uitUserName, uitPassword)
                clearBundleKeys(savedStateHandle)
                true
            } else false
        } else false
    }

    private fun onPrepareAppForTesting(
        uitUserName: String?,
        uitPassword: String?
    ) {
        viewModelScope.launch { prepareAppForTestingUseCase(uitUserName, uitPassword) }
    }

    override fun handleNotifications(bundle: Bundle?) {
        val notificationData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle?.getParcelable(
                KEY_NOTIFICATION_VIDEO_DETAILS,
                RumbleNotificationData::class.java
            )
        } else {
            bundle?.getParcelable(KEY_NOTIFICATION_VIDEO_DETAILS)
        }
        if (notificationData != null) {
            onToggleAppLaunchedFromNotification(true)
            getVideoDetails(notificationData)
            bundle?.remove(KEY_NOTIFICATION_VIDEO_DETAILS)
        }
    }

    override fun onDisplayRepostUndoWarning(repostId: Long) {
        alertDialogState.value = AlertDialogState(
            show = true,
            alertDialogReason = RumbleActivityAlertReason.UndoRepostWarning(repostId)
        )
    }

    private fun clearBundleKeys(savedStateHandle: SavedStateHandle) {
        savedStateHandle.keys().forEach { key ->
            savedStateHandle.remove<String>(key)
        }
    }

    private fun initState(savedStateHandle: SavedStateHandle) {
        val isTesting = handleLaunchAttributesForTesting(savedStateHandle)
        viewModelScope.launch(errorHandler) {
            val needLogin = loginRequiredUseCase() && isTesting.not()
            if (needLogin) emitVmEvent(RumbleEvent.NavigateToAuth)
            activityHandlerState.update {
                it.copy(
                    needLogin = needLogin,
                    readyToStart = true,
                )
            }
        }
    }

    private fun saveAppVersion() {
        viewModelScope.launch(errorHandler) {
            saveVersionCodeUseCase()
        }
    }
}