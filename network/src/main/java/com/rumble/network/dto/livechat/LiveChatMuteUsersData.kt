package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

data class LiveChatMuteUsersData(
    @SerializedName("user_ids")
    val userIdList: List<Long>
)