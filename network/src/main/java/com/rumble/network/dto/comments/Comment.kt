package com.rumble.network.dto.comments

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.video.User

data class Comment(
    @SerializedName("id")
    val commentId: Long,
    @SerializedName("user")
    val user: User?,
    @SerializedName("comment")
    val comment: String?,
    @SerializedName("date")
    val date: String?,
    @SerializedName("modified")
    val modified: String?,
    @SerializedName("comment_score")
    val commentScore: Int,
    @SerializedName("user_vote")
    val userVote: Int,
    @SerializedName("reply_id")
    val replyId: Int,
    @SerializedName("replies")
    val replies: List<Comment>?,
)
