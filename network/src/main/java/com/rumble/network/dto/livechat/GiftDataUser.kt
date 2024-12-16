package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

data class GiftDataUser(
    @SerializedName("id")
    val id: String,
    @SerializedName("username")
    val userName: String,
    @SerializedName("image.1")
    val image: String,
    @SerializedName("badges")
    val badges: List<String>?,
    @SerializedName("color")
    val color: String,
)