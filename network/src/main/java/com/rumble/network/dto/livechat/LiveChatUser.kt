package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

data class LiveChatUser(
    @SerializedName("id")
    val id: Long,
    @SerializedName("username")
    val userName: String,
    @SerializedName("image.1")
    val image: String,
    @SerializedName("badges")
    val badges: List<String>?,
    @SerializedName("color")
    val userNameColor: String
)

