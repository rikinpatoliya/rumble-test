package com.rumble.domain.video.domain.usecases

import android.app.Application
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.domain.analytics.domain.usecases.SendMediaErrorReportUseCase
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.network.session.SessionManager
import com.rumble.utils.RumbleConstants
import com.rumble.utils.RumbleConstants.WATCHED_TIME_INTERVAL
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.player.config.PlayerVideoSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CreatePlayerUseCase @Inject constructor(
    private val application: Application,
    private val userPreferenceManager: UserPreferenceManager,
    private val sessionManager: SessionManager,
    private val analyticsEventUseCase: AnalyticsEventUseCase,
    private val sendMediaErrorReportUseCase: SendMediaErrorReportUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
) {
    operator fun invoke(): RumblePlayer {
        val viewerId = runBlocking { sessionManager.viewerIdFlow.first() }
        val quality = runBlocking { userPreferenceManager.videoQuality.first() }
        val bitrate = runBlocking { userPreferenceManager.videoBitrate.first() }
        val saveVideoQuality: (PlayerVideoSource, Boolean) -> Unit = { source, isLive ->
            runBlocking {
                if (source.resolution == 0 && source.bitrate == 0) {
                    userPreferenceManager.saveLiveVideoAutoMode(true)
                } else {
                    if (isLive) userPreferenceManager.saveLiveVideoAutoMode(false)
                    userPreferenceManager.saveSelectedVideoResolution(source.resolution)
                    userPreferenceManager.saveSelectedVideoBitrate(source.bitrate)
                }
            }
        }
        val useAutoQualityForLiveVideo =
            runBlocking { userPreferenceManager.liveVideoUseAutoFlow.first() }
        return RumblePlayer(
            viewerId = viewerId,
            applicationContext = application,
            defaultVideoResolution = quality,
            defaultBitrate = bitrate,
            onVideoQualityChanged = saveVideoQuality,
            livePingIntervalFlow = sessionManager.livePingIntervalFlow,
            watchedTimeInterval = WATCHED_TIME_INTERVAL,
            useAutoQualityForLiveVideo = useAutoQualityForLiveVideo,
            getAutoplayValue = { runBlocking { userPreferenceManager.autoplayFlow.first() } },
            sendAnalyticsEvent = { event, sendDebugLogs -> analyticsEventUseCase(event, sendDebugLogs) },
            sendMediaError = { errorData -> sendMediaErrorReportUseCase(errorData) },
            sendError = { tag, throwable -> unhandledErrorUseCase(tag, throwable) }
        )
    }
}