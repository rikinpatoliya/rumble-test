package com.rumble.battles.comments

import com.rumble.domain.channels.channeldetails.domain.domainmodel.CommentAuthorEntity
import com.rumble.domain.feed.domain.domainmodel.comments.CommentEntity
import com.rumble.domain.livechat.domain.domainmodel.LiveChatChannelEntity
import com.rumble.domain.profile.domainmodel.UserProfileEntity
import com.rumble.videoplayer.player.config.ReportType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface CommentsHandler {
    val commentsUIState: StateFlow<CommentsUIState>
        get() = MutableStateFlow(CommentsUIState())
    fun onCloseAddComment()
    fun onReplies(commentEntity: CommentEntity)
    fun onDelete(commentEntity: CommentEntity)
    fun onReport(commentEntity: CommentEntity)
    fun report(commentEntity: CommentEntity, reportType: ReportType)
    fun onReplyToComment(commentEntity: CommentEntity)
    fun onLikeComment(commentEntity: CommentEntity)
    fun onDeleteAction(commentEntity: CommentEntity)
    fun onCommentChanged(comment: String)
    fun onSubmitComment()
    fun onKeepWriting()
    fun onDiscard(navigate: Boolean)
    fun onVerifyEmailForComments()
    fun onRequestVerificationLink()
    fun onCheckVerificationStatus()
    fun onLiveChatAuthorSelected(commentAuthorEntity: CommentAuthorEntity)
    fun onLiveChatThumbnailTap(channels: List<LiveChatChannelEntity>)
}

data class CommentsUIState(
    val userProfile: UserProfileEntity? = null,
    val commentList: List<CommentEntity>? = null,
    val commentNumber: Long = 0,
    val commentsDisabled: Boolean = false,
    val currentComment: String = "",
    val commentToReply: CommentEntity? = null,
    val hasPremiumRestriction: Boolean = false,
)