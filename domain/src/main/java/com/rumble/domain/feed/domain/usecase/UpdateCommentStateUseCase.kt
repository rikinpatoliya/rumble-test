package com.rumble.domain.feed.domain.usecase

import com.rumble.domain.feed.domain.domainmodel.comments.CommentEntity
import com.rumble.network.session.SessionManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UpdateCommentStateUseCase @Inject constructor(
    private val sessionManager: SessionManager,
) {
    suspend operator fun invoke(comment: CommentEntity): CommentEntity {
        val userId = sessionManager.userIdFlow.first()
        return updateCommentState(comment, userId)
    }

    private fun updateCommentState(comment: CommentEntity, userId: String): CommentEntity =
        comment.copy(
            currentUserComment = comment.authorId == userId,
            repliedByCurrentUser = comment.replayList?.any { reply -> reply.authorId == userId }
                ?: false,
            replayList = comment.replayList?.map { updateCommentState(it, userId) }
        )
}