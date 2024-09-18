package com.rumble.domain.feed.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.feed.domain.domainmodel.video.VideoDetailsResult
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.model.repository.FeedRepository
import com.rumble.network.queryHelpers.Options
import javax.inject.Inject

class GetVideoDetailsUseCase @Inject constructor(
    private val feedRepository: FeedRepository,
    private val updateCommentStateUseCase: UpdateCommentStateUseCase,
    private val updateVideoSourceListUseCase: UpdateVideoSourceListUseCase,
    override val rumbleErrorUseCase: RumbleErrorUseCase
) : RumbleUseCase {
    suspend operator fun invoke(videoId: Long): VideoEntity? {
        val videoDetailsResult = feedRepository.fetchVideoDetails(
            videoId,
            listOf(Options.EXTENDED, Options.RELATED, Options.COMMENTS, Options.CATEGORIES, Options.WATCHING_PROGRESS)
        )
        return extractVideo(videoDetailsResult)
    }

    suspend operator fun invoke(videoUrl: String): VideoEntity? {
        val videoDetailsResult = feedRepository.fetchVideoDetails(
            videoUrl,
            listOf(Options.EXTENDED, Options.RELATED, Options.COMMENTS, Options.CATEGORIES)
        )
        return extractVideo(videoDetailsResult)
    }

    private suspend fun extractVideo(videoDetailsResult: VideoDetailsResult): VideoEntity? =
        when (videoDetailsResult) {
            is VideoDetailsResult.VideoDetailsError -> {
                rumbleErrorUseCase(videoDetailsResult.rumbleError)
                null
            }

            is VideoDetailsResult.VideoDetailsSuccess -> {
                val video = videoDetailsResult.videoEntity
                video.copy(
                    videoSourceList = updateVideoSourceListUseCase(video),
                    commentList = video.commentList?.map { updateCommentStateUseCase(it) }
                )
            }
        }
}