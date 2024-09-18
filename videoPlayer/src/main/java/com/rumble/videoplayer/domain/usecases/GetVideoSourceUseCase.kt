package com.rumble.videoplayer.domain.usecases

import com.rumble.videoplayer.player.config.DefaultResolution
import com.rumble.videoplayer.player.config.PlayerVideoSource
import javax.inject.Inject

class GetVideoSourceUseCase @Inject constructor() {

    operator fun invoke(
        sourceList: List<PlayerVideoSource>,
        resolution: Int?,
        bitrate: Int?,
        defaultResolution: DefaultResolution,
        useLowQuality: Boolean = false,
        useAutoQualityForLiveVideo: Boolean = false
    ): PlayerVideoSource? {
        val autoQuality = sourceList.find { it.resolution == 0 && it.bitrate == 0 }
        return if (useAutoQualityForLiveVideo && autoQuality != null) {
            autoQuality
        } else if (resolution != null && bitrate != null) {
            sourceList
                .sortedBy { it.getBitrateDistanceFrom(bitrate) }
                .minByOrNull { it.getResolutionDistanceFrom(resolution) }
        } else if (useLowQuality) {
            sourceList.minByOrNull { it.resolution }
        } else {
            sourceList.find { it.resolution == defaultResolution.value }
        }
    }
}