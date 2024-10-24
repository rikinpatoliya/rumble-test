package com.rumble.domain.video.domain.usecases

import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.network.di.Publisher
import com.rumble.network.dto.LiveStreamStatus
import com.rumble.network.queryHelpers.PublisherId
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.player.VideoStartMethod
import javax.inject.Inject

class UpdateVideoPlayerSourceUseCase @Inject constructor(
    private val createRumbleVideoUseCase: CreateRumbleVideoUseCase,
    private val fetchRelatedVideoUseCase: FetchRelatedVideoListUseCase,
    @Publisher private val publisherId: PublisherId
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
        saveLastPosition: (Long, Long) -> Unit = { _, _ -> },
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
                    publisherId = publisherId,
                    screenId = screenId,
                    includeMetadata = false,
                    requestLiveGateData = requestLiveGateData,
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
                    publisherId = publisherId,
                    screenId = screenId,
                    includeMetadata = videoEntity.includeMetadata,
                    requestLiveGateData = requestLiveGateData,
                ),
                onSaveLastPosition = if (positionCanBeSaved(videoEntity.livestreamStatus)) { position, videoId ->
                    saveLastPosition(position, videoId)
                } else null,
                updatedRelatedVideoList = updatedRelatedVideoList
            )
        }
    }

    private fun positionCanBeSaved(status: LiveStreamStatus) =
        status == LiveStreamStatus.UNKNOWN || status == LiveStreamStatus.ENDED
}
