package com.rumble.domain.feed.domain.usecase

import com.rumble.domain.feed.domain.domainmodel.comments.CommentEntity
import com.rumble.network.session.SessionManager
import com.rumble.utils.RumbleConstants
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UpdateCommentStateUseCase @Inject constructor(
    private val sessionManager: SessionManager,
) {
    suspend operator fun invoke(comment: CommentEntity): CommentEntity {
        val userId = sessionManager.userIdFlow.first()
        return updateCommentState(comment, userId, 1)
    }

    private fun updateCommentState(
        comment: CommentEntity,
        userId: String,
        commentLevel: Int
    ): CommentEntity =
        comment.copy(
            currentUserComment = comment.authorId == userId,
            repliedByCurrentUser = comment.replayList?.any { reply -> reply.authorId == userId }
                ?: false,
            replayList = comment.replayList?.map {
                updateCommentState(it, userId, commentLevel + 1)
            },
            replyAllowed = commentLevel < RumbleConstants.MAX_COMMENT_LEVEL
        )
}