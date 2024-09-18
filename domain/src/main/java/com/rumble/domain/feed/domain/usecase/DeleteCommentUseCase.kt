package com.rumble.domain.feed.domain.usecase

import com.rumble.domain.feed.domain.domainmodel.comments.CommentResult
import com.rumble.domain.feed.model.repository.FeedRepository
import javax.inject.Inject

class DeleteCommentUseCase @Inject constructor(
    private val feedRepository: FeedRepository
) {
    suspend operator fun invoke(commentId: Long): CommentResult =
        feedRepository.deleteComment(commentId)
}