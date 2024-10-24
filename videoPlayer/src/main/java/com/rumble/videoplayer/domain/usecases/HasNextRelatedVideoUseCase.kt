package com.rumble.videoplayer.domain.usecases

import com.rumble.videoplayer.player.RumbleVideo
import javax.inject.Inject

class HasNextRelatedVideoUseCase @Inject constructor() {
    operator fun invoke(
        relatedVideoList: List<RumbleVideo>,
        currentVideo: RumbleVideo?,
        autoPlay: Boolean
    ): Boolean =
        relatedVideoList.isNotEmpty() &&
            (relatedVideoList.last().videoId != currentVideo?.videoId) &&
            autoPlay &&
            currentVideo?.hasLiveGate == false
}