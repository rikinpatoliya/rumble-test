package com.rumble.battles.landing

import android.app.Application
import android.content.pm.PackageManager
import android.support.v4.media.session.MediaSessionCompat
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.analytics.PushNotificationHandlingFailedEvent
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.dialogs.AlertDialogState
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.usecase.GetSensorBasedOrientationChangeEnabledUseCase
import com.rumble.domain.landing.usecases.GetUserCookiesUseCase
import com.rumble.domain.landing.usecases.PipIsAvailableUseCase
import com.rumble.domain.landing.usecases.SilentLoginUseCase
import com.rumble.domain.landing.usecases.TransferUserDataUseCase
import com.rumble.domain.landing.usecases.UpdateMediaSessionUseCase
import com.rumble.domain.logging.domain.usecase.InitProductionLoggingUseCase
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
import com.rumble.utils.extension.isScreenOn
import com.rumble.videoplayer.player.PlayerTargetChangeListener
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.player.config.PlayerTarget
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
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
    var dynamicOrientationChangeDisabled: Boolean
    val sensorBasedOrientationChangeEnabled: Boolean
    val colorMode: Flow<ColorMode>
    val isLaunchedFromNotification: Boolean

    fun onPrepareAppForTesting(uitUserName: String?, uitPassword: String?)
    fun onAppLaunchedFromNotification()
    suspend fun pipIsAvailable(packageManager: PackageManager): Boolean
    suspend fun backgroundSoundIsAvailable(): Boolean
    suspend fun getCookies(): String
    fun getVideoDetails(rumbleNotificationData: RumbleNotificationData)
    fun startObserveCookies()
    fun initLogging()
    fun initMediaSession(session: MediaSessionCompat)
    fun onError(e: Throwable)
    fun onAppPaused()
    fun onEnterPipMode()
    fun onExitPipMode()
    fun clearNotifications()
    fun loadNotificationState()
    fun disableDynamicOrientationChangeBasedOnDeviceType()
    fun onShowAlertDialog(reason: RumbleActivityAlertReason)
    fun onDismissDialog()
    fun onDeepLinkNavigated()
    fun enableContentLoad()
    fun onPremiumPurchased()
}


sealed class RumbleEvent {
    data class NavigateToVideoDetailsFromNotification(val videoEntity: VideoEntity) : RumbleEvent()
    object UnexpectedError : RumbleEvent()
    object PipModeEntered : RumbleEvent()
    object DisableDynamicOrientationChangeBasedOnDeviceType : RumbleEvent()
    object PremiumPurchased : RumbleEvent()
}

sealed class RumbleActivityAlertReason : AlertDialogReason {
    object VideoDetailsFromNotificationFailedReason : RumbleActivityAlertReason()
    object DeleteWatchHistoryConfirmationReason : RumbleActivityAlertReason()
    data class DeletePlayListConfirmationReason(val playListId: String) : RumbleActivityAlertReason()
    data class UnfollowConfirmationReason(val channel: ChannelDetailsEntity) : RumbleActivityAlertReason()
    object PremiumPurchaseMade: RumbleActivityAlertReason()
    object SubscriptionNotAvailable: RumbleActivityAlertReason()
}

data class ActivityHandlerState(
    val hasUnreadNotifications: Boolean = false
)

@HiltViewModel
class RumbleActivityViewModel @Inject constructor(
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
    private val getSensorBasedOrientationChangeEnabledUseCase: GetSensorBasedOrientationChangeEnabledUseCase,
    private val getUserHasUnreadNotificationsUseCase: GetUserHasUnreadNotificationsUseCase,
    private val prepareAppForTestingUseCase: PrepareAppForTestingUseCase,
    application: Application,
) : AndroidViewModel(application), RumbleActivityHandler, PlayerTargetChangeListener {

    override var isLaunchedFromNotification: Boolean = false
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
    override var dynamicOrientationChangeDisabled: Boolean = true
    override val sensorBasedOrientationChangeEnabled: Boolean
        get() = getSensorBasedOrientationChangeEnabledUseCase()

    private var mediaSession: MediaSessionCompat? = null
    private var notificationGuid: String = ""
    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
    }

    init {
        viewModelScope.launch(errorHandler) {
            generateViewerIdUseCase()
            sessionManager.saveUniqueSession(UUID.randomUUID().toString())
        }
        loadNotificationState()
    }

    override fun onPlayerTargetChanged(currentTarget: PlayerTarget) {
        mediaSession?.let {
            updateMediaSessionUseCase(it, currentPlayer, currentTarget != PlayerTarget.AD)
        }
    }

    override fun onPrepareAppForTesting(
        uitUserName: String?,
        uitPassword: String?
    ) {
        viewModelScope.launch(errorHandler) {
            prepareAppForTestingUseCase(uitUserName, uitPassword)
        }
    }

    override fun onAppLaunchedFromNotification() {
        isLaunchedFromNotification = true
    }

    override suspend fun pipIsAvailable(packageManager: PackageManager): Boolean =
        pipIsAvailableUseCase()
            && currentPlayer != null

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

    override fun getVideoDetails(rumbleNotificationData: RumbleNotificationData) {
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
            }
        }
    }

    override fun onEnterPipMode() {
        mediaSession?.let {
            updateMediaSessionUseCase(it, currentPlayer, currentPlayer?.playerTarget?.value != PlayerTarget.AD)
        }
        currentPlayer?.hideControls()
        currentPlayer?.enableMidRolls = false
        emitVmEvent(RumbleEvent.PipModeEntered)
    }

    override fun onExitPipMode() {
        currentPlayer?.enableMidRolls = true
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

    override fun onDeepLinkNavigated() {
        currentPlayer?.pauseVideo()
    }

    override fun enableContentLoad() {
        viewModelScope.launch { sessionManager.allowContentLoadFlow(true) }
    }

    override fun onPremiumPurchased() {
        emitVmEvent(RumbleEvent.PremiumPurchased)
    }
}