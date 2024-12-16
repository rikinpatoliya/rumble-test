package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

data class GiftDataChannel(
    @SerializedName("id")
    val id: String,
    @SerializedName("username")
    val userName: String,
    @SerializedName("image.1")
    val image: String,
)