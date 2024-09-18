package com.rumble.domain.video.domain.usecases

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.video.domain.domainmodel.FetchRelatedVideoListResult
import com.rumble.domain.video.model.repository.VideoRepository
import javax.inject.Inject

class FetchRelatedVideoListUseCase @Inject constructor(
    private val videoRepository: VideoRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase
) : RumbleUseCase {
    suspend operator fun invoke(videoId: Long): List<VideoEntity> {
        return when (val result = videoRepository.fetchRelatedVideoList(videoId)) {
            is FetchRelatedVideoListResult.Success -> {
                result.videoList
            }

            is FetchRelatedVideoListResult.Failure -> {
                rumbleErrorUseCase(result.rumbleError)
                emptyList()
            }
        }
    }
}