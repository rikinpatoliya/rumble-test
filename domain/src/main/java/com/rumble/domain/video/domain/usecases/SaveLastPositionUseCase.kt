package com.rumble.domain.video.domain.usecases

import com.rumble.domain.video.model.repository.VideoRepository
import com.rumble.network.session.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class SaveLastPositionUseCase @Inject constructor(
    private val sessionManager: SessionManager,
    private val videoRepository: VideoRepository,
) {

    operator fun invoke(lastPosition: Long, videoId: Long) {
        videoRepository.saveLastPosition(
            userId = runBlocking { sessionManager.userIdFlow.first() },
            videoId = videoId,
            position = lastPosition
        )
    }
}