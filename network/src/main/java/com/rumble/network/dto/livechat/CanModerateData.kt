package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

data class CanModerateData(
    @SerializedName("user_ids")
    val userIdList: List<Long>,
    @SerializedName("can_moderate")
    val canModerate: Boolean
)