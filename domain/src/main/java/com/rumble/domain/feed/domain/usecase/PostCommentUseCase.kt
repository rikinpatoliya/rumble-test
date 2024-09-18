package com.rumble.domain.feed.domain.usecase

import com.rumble.domain.feed.domain.domainmodel.comments.CommentResult
import com.rumble.domain.feed.domain.domainmodel.comments.UserComment
import com.rumble.domain.feed.model.repository.FeedRepository
import javax.inject.Inject

class PostCommentUseCase @Inject constructor(
    private val feedRepository: FeedRepository,
    private val validComment: ValidCommentUseCase,
) {
    suspend operator fun invoke(
        comment: String,
        videoId: Long,
        commentId: Long?,
    ): CommentResult {
        val preparedComment = comment.trim()
        return if (validComment(comment).not()) CommentResult(tooShort = true)
        else feedRepository.postComment(UserComment(preparedComment, videoId, commentId))
    }
}