package com.rumble.network.dto.comments

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.creator.UserLight

data class UserCommentResponse(
    @SerializedName("user")
    val user: UserLight?,
    @SerializedName("data")
    val data: UserCommentData
)