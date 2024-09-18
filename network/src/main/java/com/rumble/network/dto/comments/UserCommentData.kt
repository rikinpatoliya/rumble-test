package com.rumble.network.dto.comments

import com.google.gson.annotations.SerializedName

data class UserCommentData(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("comment_id")
    val commentId: Long?,
    @SerializedName("comment_text")
    val commentText: String?,
    @SerializedName("error")
    val error: String?
)
