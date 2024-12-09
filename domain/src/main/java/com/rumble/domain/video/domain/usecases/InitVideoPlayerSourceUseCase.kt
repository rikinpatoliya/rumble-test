package com.rumble.domain.video.domain.usecases

import com.rumble.analytics.VideoViewEvent
import com.rumble.domain.analytics.domain.domainmodel.videoDetailsScreen
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.domain.feed.domain.usecase.GetVideoDetailsUseCase
import com.rumble.domain.rumbleads.domain.usecase.FetchVideoAdListUseCase
import com.rumble.domain.rumbleads.domain.usecase.SendAdEventUseCase
import com.rumble.domain.timerange.domain.usecases.SaveTimeRangeUseCase
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.player.VideoStartMethod
import com.rumble.videoplayer.player.config.LiveVideoReportResult
import com.rumble.videoplayer.player.config.StreamStatus
import com.rumble.videoplayer.player.config.VideoScope
import com.rumble.videoplayer.player.internal.notification.RumblePlayList
import javax.inject.Inject

class InitVideoPlayerSourceUseCase @Inject constructor(
    private val getVideoDetailsUseCase: GetVideoDetailsUseCase,
    private val createPlayerUseCase: CreatePlayerUseCase,
    private val createRumbleVideoUseCase: CreateRumbleVideoUseCase,
    private val reportLiveVideoUseCase: ReportLiveVideoUseCase,
    private val analyticsEventUseCase: AnalyticsEventUseCase,
    private val saveTimeRangeUseCase: SaveTimeRangeUseCase,
    private val fetchRelatedVideoUseCase: FetchRelatedVideoListUseCase,
    private val fetchVideoAdListUseCase: FetchVideoAdListUseCase,
    private val sendAdEventUseCase: SendAdEventUseCase,
    private val lastPositionCanBeSavedUseCase: LastPositionCanBeSavedUseCase,
) {
    suspend operator fun invoke(
        videoId: Long,
        screenId: String,
        loopWhenFinished: Boolean = false,
        restrictBackground: Boolean = false,
        applyLastPosition: Boolean = true,
        useLowQuality: Boolean = false,
        autoplay: Boolean = false,
        showAds: Boolean = false,
        requestLiveGateData: Boolean = false,
        videoScope: VideoScope,
        videoStartMethod: VideoStartMethod = VideoStartMethod.URL_PROVIDED,
        saveLastPosition: (Long, Long) -> Unit = { _, _ -> },
        liveVideoReport: ((Long, LiveVideoReportResult) -> Unit)? = null,
        onVideoSizeDefined: ((Int, Int) -> Unit)? = null,
        onNextVideo: ((Long, String, Boolean) -> Unit)? = null,
        sendInitialPlaybackEvent: (() -> Unit)? = null,
        onPremiumCountdownFinished:  (() -> Unit)? = null,
        onVideoReady: ((Long, RumblePlayer) -> Unit)? = null,
        preRollAdLoadingEvent: (() -> Unit)? = null,
        preRollAdStartedEvent: (() -> Unit)? = null
    ): RumblePlayer {
        val videoEntity = getVideoDetailsUseCase(videoId)
        val relatedVideoList =
            if (autoplay) fetchRelatedVideoUseCase(videoId = videoId).map {
                createRumbleVideoUseCase(
                    videoEntity = it,
                    restrictBackground = restrictBackground,
                    loopWhenFinished = loopWhenFinished,
                    applyLastPosition = applyLastPosition,
                    videoStartMethod = videoStartMethod,
                    useLowQuality = useLowQuality,
                    relatedVideoList = emptyList(),
                    screenId = screenId,
                    includeMetadata = false,
                    requestLiveGateData = requestLiveGateData,
                    videoScope = videoScope,
                )
            } else emptyList()
        val videoPlayer = createPlayerUseCase()
        videoEntity?.let {
            videoPlayer.setVideo(
                video = createRumbleVideoUseCase(
                    videoEntity = videoEntity,
                    restrictBackground = restrictBackground,
                    loopWhenFinished = loopWhenFinished,
                    applyLastPosition = applyLastPosition,
                    videoStartMethod = videoStartMethod,
                    useLowQuality = useLowQuality,
                    relatedVideoList = relatedVideoList,
                    screenId = screenId,
                    includeMetadata = videoEntity.includeMetadata,
                    requestLiveGateData = requestLiveGateData,
                    videoScope = videoScope,
                ),
                reportLiveVideo = reportLiveVideoUseCase::invoke,
                onLiveVideoReport = liveVideoReport,
                saveLastPosition = if (lastPositionCanBeSavedUseCase(videoEntity.livestreamStatus)) { position, videoId ->
                    saveLastPosition(position, videoId)
                } else null,
                onVideoSizeDefined = onVideoSizeDefined,
                onTrackWatchedTime = {
                    if (screenId == videoDetailsScreen)
                        analyticsEventUseCase(VideoViewEvent(screenId), true)
                },
                onTimeRange = saveTimeRangeUseCase::invoke,
                onNextVideo = onNextVideo,
                fetchPreRollList = if (showAds) fetchVideoAdListUseCase::invoke else null,
                reportAdEvent = if (showAds) sendAdEventUseCase::invoke else null,
                preRollAdLoadingEvent = preRollAdLoadingEvent,
                preRollAdStartedEvent = preRollAdStartedEvent,
                playList = null,
                sendInitialPlaybackEvent = sendInitialPlaybackEvent,
                onPremiumCountdownFinished = onPremiumCountdownFinished,
                onVideoReady = onVideoReady,
            )
        }
        return videoPlayer
    }

    operator fun invoke(
        playList: RumblePlayList,
        screenId: String,
        showAds: Boolean = true,
        saveLastPosition: (Long, Long) -> Unit = { _, _ -> },
        liveVideoReport: ((Long, LiveVideoReportResult) -> Unit)? = null,
        onVideoSizeDefined: ((Int, Int) -> Unit)? = null,
        onNextVideo: ((Long, String, Boolean) -> Unit)? = null,
        sendInitialPlaybackEvent: (() -> Unit)? = null,
        onPremiumCountdownFinished:  (() -> Unit)? = null,
        onVideoReady: ((Long, RumblePlayer) -> Unit)? = null,
        preRollAdLoadingEvent: (() -> Unit)? = null,
        preRollAdStartedEvent: (() -> Unit)? = null
    ): RumblePlayer {
        val initialVideo = playList.videoList.first()
        return createPlayerUseCase().apply {
            setVideo(
                video = initialVideo,
                reportLiveVideo = reportLiveVideoUseCase::invoke,
                onLiveVideoReport = liveVideoReport,
                saveLastPosition = if (initialVideo.streamStatus == StreamStatus.NotStream) { position, videoId ->
                    saveLastPosition(position, videoId)
                } else null,
                onVideoSizeDefined = onVideoSizeDefined,
                onTrackWatchedTime = {
                    if (screenId == videoDetailsScreen)
                        analyticsEventUseCase(VideoViewEvent(screenId), true)
                },
                onTimeRange = saveTimeRangeUseCase::invoke,
                onNextVideo = onNextVideo,
                fetchPreRollList = if (showAds) fetchVideoAdListUseCase::invoke else null,
                reportAdEvent = if (showAds) sendAdEventUseCase::invoke else null,
                playList = playList,
                sendInitialPlaybackEvent = sendInitialPlaybackEvent,
                onPremiumCountdownFinished = onPremiumCountdownFinished,
                onVideoReady = onVideoReady,
                preRollAdLoadingEvent = preRollAdLoadingEvent,
                preRollAdStartedEvent = preRollAdStartedEvent
            )
        }
    }
}


