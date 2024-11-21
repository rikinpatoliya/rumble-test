package com.rumble.network.dto.repost

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.login.UserState

data class DeleteRepostResponse(
    @SerializedName("user")
    val user: UserState,
    @SerializedName("data")
    val data: DeleteRepostData,
)

data class DeleteRepostData(
    @SerializedName("success")
    val success: Boolean
)
