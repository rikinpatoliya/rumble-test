package com.rumble.domain.feed.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.feed.domain.domainmodel.comments.CommentEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoDetailsResult
import com.rumble.domain.feed.model.repository.FeedRepository
import com.rumble.network.queryHelpers.Options
import javax.inject.Inject

class GetVideoCommentsUseCase @Inject constructor(
    private val feedRepository: FeedRepository,
    private val updateCommentStateUseCase: UpdateCommentStateUseCase,
    override val rumbleErrorUseCase: RumbleErrorUseCase
) : RumbleUseCase {
    suspend operator fun invoke(videoId: Long): List<CommentEntity> {
        val comments: List<CommentEntity> =
            when (val videoDetailsResult = feedRepository.fetchVideoDetails(
                videoId,
                listOf(Options.COMMENTS)
            )) {
                is VideoDetailsResult.VideoDetailsError -> {
                    rumbleErrorUseCase(videoDetailsResult.rumbleError)
                    emptyList()
                }
                is VideoDetailsResult.VideoDetailsSuccess -> videoDetailsResult.videoEntity.commentList
                    ?: emptyList()
            }
        return comments.map { comment -> updateCommentStateUseCase(comment) }.sortedByDescending { it.date }
    }
}