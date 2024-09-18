package com.rumble.domain.video.domain.usecases

import com.rumble.domain.video.model.repository.VideoRepository
import com.rumble.network.session.SessionManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetLastPositionUseCase @Inject constructor(
    private val sessionManager: SessionManager,
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(videoId: Long): Long =
        videoRepository.getLastPosition(userId = sessionManager.userIdFlow.first(), videoId) ?: 0
}