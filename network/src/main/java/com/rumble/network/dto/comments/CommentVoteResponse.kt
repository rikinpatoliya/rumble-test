package com.rumble.network.dto.comments

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.video.User

data class CommentVoteResponse(
    @SerializedName("user")
    val user: User,
    @SerializedName("data")
    val data: CommentVoteData
)
