package com.rumble.domain.video.domain.usecases

import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoStatus
import com.rumble.domain.settings.domain.domainmodel.BackgroundPlay
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.network.dto.LiveStreamStatus
import com.rumble.network.queryHelpers.PublisherId
import com.rumble.network.session.SessionManager
import com.rumble.videoplayer.player.RumbleLiveStreamStatus
import com.rumble.videoplayer.player.RumbleVideo
import com.rumble.videoplayer.player.VideoStartMethod
import com.rumble.videoplayer.player.config.BackgroundMode
import com.rumble.videoplayer.player.config.PlayerVideoSource
import com.rumble.videoplayer.player.config.RumbleVideoStatus
import com.rumble.videoplayer.player.config.StreamStatus
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CreateRumbleVideoUseCase @Inject constructor(
    private val userPreferenceManager: UserPreferenceManager,
    private val defineCurrentWatchPositionUseCase: DefineCurrentWatchPositionUseCase,
    private val buildVideoUrlWithMetadataParamsUseCase: BuildVideoUrlWithMetadataParamsUseCase,
    private val sessionManager: SessionManager,
) {
    suspend operator fun invoke(
        videoEntity: VideoEntity,
        restrictBackground: Boolean,
        loopWhenFinished: Boolean,
        applyLastPosition: Boolean,
        videoStartMethod: VideoStartMethod,
        useLowQuality: Boolean,
        relatedVideoList: List<RumbleVideo>,
        publisherId: PublisherId,
        screenId: String,
        includeMetadata: Boolean,
    ): RumbleVideo {
        val backgroundPlay =
            if (restrictBackground) BackgroundPlay.OFF
            else userPreferenceManager.backgroundPlayFlow.first()
        val lastPosition = defineCurrentWatchPositionUseCase(videoEntity, applyLastPosition)

        return RumbleVideo(
            videoId = videoEntity.id,
            videoList = videoEntity.videoSourceList.map {

                val videoUrl = if (includeMetadata) buildVideoUrlWithMetadataParamsUseCase(
                    videoEntity.id,
                    it.videoUrl,
                    videoEntity.videoWidth,
                    videoEntity.videoHeight,
                    it.resolution,
                    videoEntity.categoriesList?.firstOrNull(),
                    videoEntity.ageRestricted,
                    videoEntity.channelId
                ) else it.videoUrl

                PlayerVideoSource(
                    videoUrl = videoUrl,
                    type = it.type,
                    resolution = it.resolution,
                    bitrate = it.bitrate,
                    qualityText = it.qualityText,
                    bitrateText = it.bitrateText
                )
            },
            videoThumbnailUri = videoEntity.videoThumbnail,
            supportsDvr = videoEntity.supportsDvr,
            watchingNow = videoEntity.watchingNow,
            backgroundMode = when (backgroundPlay) {
                BackgroundPlay.SOUND -> BackgroundMode.On
                else -> BackgroundMode.Off
            },
            loop = loopWhenFinished or (videoEntity.livestreamStatus == LiveStreamStatus.OFFLINE),
            title = videoEntity.title,
            description = videoEntity.description ?: "",
            streamStatus = when (videoEntity.livestreamStatus) {
                LiveStreamStatus.UNKNOWN, LiveStreamStatus.ENDED -> StreamStatus.NotStream
                LiveStreamStatus.LIVE -> StreamStatus.LiveStream
                LiveStreamStatus.OFFLINE -> StreamStatus.OfflineStream
            },
            lastPosition = lastPosition,
            method = videoStartMethod,
            useLowQuality = useLowQuality,
            ageRestricted = videoEntity.ageRestricted,
            channelName = videoEntity.channelName,
            channelIcon = videoEntity.channelThumbnail,
            displayVerifiedBadge = videoEntity.verifiedBadge,
            channelFollowers = videoEntity.channelFollowers,
            relatedVideoList = relatedVideoList,
            channelId = videoEntity.channelId,
            duration = videoEntity.duration,
            publisherId = publisherId,
            screenId = screenId,
            verifiedBadge = videoEntity.verifiedBadge,
            uploadDate = videoEntity.uploadDate,
            viewsNumber = videoEntity.viewsNumber,
            videoStatus = when (videoEntity.videoStatus) {
                VideoStatus.UPLOADED -> RumbleVideoStatus.UPLOADED
                VideoStatus.UPCOMING -> RumbleVideoStatus.UPCOMING
                VideoStatus.SCHEDULED -> RumbleVideoStatus.SCHEDULED
                VideoStatus.STARTING -> RumbleVideoStatus.STARTING
                VideoStatus.LIVE -> RumbleVideoStatus.LIVE
                VideoStatus.STREAMED -> RumbleVideoStatus.STREAMED
            },
            likeNumber = videoEntity.likeNumber,
            dislikeNumber = videoEntity.dislikeNumber,
            scheduledDate = videoEntity.scheduledDate,
            userId = sessionManager.userIdFlow.first(),
            isPremiumExclusiveContent = videoEntity.isPremiumExclusiveContent,
            livestreamStatus = when (videoEntity.livestreamStatus) {
                LiveStreamStatus.UNKNOWN -> RumbleLiveStreamStatus.UNKNOWN
                LiveStreamStatus.ENDED -> RumbleLiveStreamStatus.ENDED
                LiveStreamStatus.OFFLINE -> RumbleLiveStreamStatus.OFFLINE
                LiveStreamStatus.LIVE -> RumbleLiveStreamStatus.LIVE
            },
            liveDateTime = videoEntity.liveDateTime,
            liveStreamedOn = videoEntity.liveStreamedOn
        )
    }
}