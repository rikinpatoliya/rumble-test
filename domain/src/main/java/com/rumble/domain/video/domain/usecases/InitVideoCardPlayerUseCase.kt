package com.rumble.domain.video.domain.usecases

import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.settings.domain.usecase.HasPremiumRestrictionUseCase
import com.rumble.domain.settings.domain.usecase.PlaybackInFeedsEnabledUseCase
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.player.config.LiveVideoReportResult
import com.rumble.videoplayer.player.config.VideoScope
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class InitVideoCardPlayerUseCase @Inject constructor(
    private val initVideoPlayerSourceUseCase: InitVideoPlayerSourceUseCase,
    private val saveLastPositionUseCase: SaveLastPositionUseCase,
    private val userPreferenceManager: UserPreferenceManager,
    private val playbackInFeedsEnabledUseCase: PlaybackInFeedsEnabledUseCase,
    private val hasPremiumRestrictionUseCase: HasPremiumRestrictionUseCase,
) {
    suspend operator fun invoke(
        videoEntity: VideoEntity,
        screenId: String,
        liveVideoReport: ((Long, LiveVideoReportResult) -> Unit)? = null,
    ): RumblePlayer? {
        return if (playbackInFeedsEnabledUseCase() && hasPremiumRestrictionUseCase(videoEntity).not()) {
            val soundOn = userPreferenceManager.videoCardSoundStateFlow.first()
            val player = initVideoPlayerSourceUseCase(
                videoId = videoEntity.id,
                loopWhenFinished = true,
                restrictBackground = true,
                useLowQuality = true,
                screenId = screenId,
                videoScope = VideoScope.Other,
                saveLastPosition = { lastPosition, videoId ->
                    if (soundOn) saveLastPositionUseCase(lastPosition, videoId)
                },
                requestLiveGateData = true,
                liveVideoReport = liveVideoReport
            )
            if (soundOn.not()) player.mute()
            player
        } else null
    }
}