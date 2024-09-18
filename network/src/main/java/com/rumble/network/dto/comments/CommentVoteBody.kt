package com.rumble.network.dto.comments

import com.google.gson.annotations.SerializedName

data class CommentVoteBody(
    @SerializedName("data")
    val data: CommentVoteData
)

data class CommentVoteData(
    @SerializedName("comment_id")
    val commentId: Long,
    @SerializedName("value")
    val vote: Int
)
