package com.rumble.domain.video.domain.usecases

import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.network.dto.LiveStreamStatus
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.player.VideoStartMethod
import com.rumble.videoplayer.player.config.VideoScope
import javax.inject.Inject

class UpdateVideoPlayerSourceUseCase @Inject constructor(
    private val createRumbleVideoUseCase: CreateRumbleVideoUseCase,
    private val fetchRelatedVideoUseCase: FetchRelatedVideoListUseCase,
) {

    suspend operator fun invoke(
        player: RumblePlayer,
        videoEntity: VideoEntity,
        screenId: String,
        loopWhenFinished: Boolean = false,
        restrictBackground: Boolean = false,
        applyLastPosition: Boolean = true,
        useLowQuality: Boolean = false,
        autoplay: Boolean = false,
        requestLiveGateData: Boolean = false,
        updatedRelatedVideoList: Boolean,
        videoStartMethod: VideoStartMethod = VideoStartMethod.URL_PROVIDED,
        videoScope: VideoScope,
        saveLastPosition: (Long, Long) -> Unit = { _, _ -> },
        onPremiumCountdownFinished:  (() -> Unit)? = null,
        onVideoReady: ((Long, RumblePlayer) -> Unit)? = null,
    ): RumblePlayer {
        val relatedVideoList =
            if (updatedRelatedVideoList) fetchRelatedVideoUseCase(videoId = videoEntity.id).map {
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
                    videoScope= videoScope,
                )
            } else emptyList()
        return player.apply {
            updateCurrentVideo(
                autoPlay = autoplay,
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
                    videoScope= videoScope,
                ),
                onSaveLastPosition = if (positionCanBeSaved(videoEntity.livestreamStatus)) { position, videoId ->
                    saveLastPosition(position, videoId)
                } else null,
                updatedRelatedVideoList = updatedRelatedVideoList,
                onPremiumCountdownFinished = onPremiumCountdownFinished,
                onVideoReady = onVideoReady,
            )
        }
    }

    private fun positionCanBeSaved(status: LiveStreamStatus) =
        status == LiveStreamStatus.UNKNOWN || status == LiveStreamStatus.ENDED
}
