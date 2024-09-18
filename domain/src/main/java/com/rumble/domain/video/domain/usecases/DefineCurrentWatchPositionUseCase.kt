package com.rumble.domain.video.domain.usecases

import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.utils.RumbleConstants.WATCHED_TIME_OFFSET
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DefineCurrentWatchPositionUseCase @Inject constructor(
    private val getLastPositionUseCase: GetLastPositionUseCase
) {
    suspend operator fun invoke(
        videoEntity: VideoEntity,
        applyLastPosition: Boolean,
    ): Long {
        val lastPosition = if (applyLastPosition) {
            videoEntity.lastPositionSeconds?.let { TimeUnit.SECONDS.toMillis(it) }
                ?: getLastPositionUseCase(videoEntity.id)
        } else 0
        val watchTimeLimit = TimeUnit.SECONDS.toMillis(videoEntity.duration - WATCHED_TIME_OFFSET)
        return if (lastPosition < watchTimeLimit) lastPosition else 0
    }
}