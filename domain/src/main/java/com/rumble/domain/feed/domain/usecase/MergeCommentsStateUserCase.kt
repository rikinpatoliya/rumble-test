package com.rumble.domain.feed.domain.usecase

import com.rumble.domain.feed.domain.domainmodel.comments.CommentEntity
import javax.inject.Inject

class MergeCommentsStateUserCase @Inject constructor() {

    operator fun invoke(
        repliedComment: CommentEntity?,
        oldState: List<CommentEntity>,
        newState: List<CommentEntity>
    ): List<CommentEntity> {
        val expended = oldState.filter { it.displayReplies }
        val updated = newState.map { comment ->
            comment.copy(
                displayReplies = expended.any { it.commentId == comment.commentId } || comment.commentId == repliedComment?.commentId,
                replayList = invoke(
                    repliedComment,
                    oldState.find { it.commentId == comment.commentId }?.replayList ?: emptyList(),
                    comment.replayList ?: emptyList()
                )
            )
        }
        return updated
    }
}