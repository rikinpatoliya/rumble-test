package com.rumble.domain.feed.domain.usecase

import com.rumble.domain.feed.domain.domainmodel.comments.CommentEntity
import javax.inject.Inject

class UpdateCommentListReplyVisibilityUseCase @Inject constructor() {
    operator fun invoke(
        changedCommentId: Long,
        commentEntity: CommentEntity
    ): CommentEntity =
        commentEntity.copy(
            displayReplies = if (commentEntity.commentId == changedCommentId) commentEntity.displayReplies.not() else commentEntity.displayReplies,
            replayList = commentEntity.replayList?.map { invoke(changedCommentId, it) })
}