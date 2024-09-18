package com.rumble.videoplayer.domain.usecases

import com.rumble.videoplayer.player.RumbleVideo
import javax.inject.Inject

class GetNextRelatedVideoUseCase @Inject constructor() {
    operator fun invoke(
        relatedVideoList: List<RumbleVideo>,
        currentVideo: RumbleVideo?
    ): RumbleVideo? {
        val currentIndex = relatedVideoList.indexOf(currentVideo)
        return if (currentIndex != relatedVideoList.lastIndex) relatedVideoList[currentIndex + 1]
        else null
    }
}