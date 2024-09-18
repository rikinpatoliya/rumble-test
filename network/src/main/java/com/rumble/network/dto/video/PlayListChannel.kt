package com.rumble.network.dto.video

import com.google.gson.annotations.SerializedName

data class PlayListChannel(
    @SerializedName("id")
    val channelId: Int,
    @SerializedName("url")
    val url: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("picture")
    val picture: String?,
    @SerializedName("followers")
    val channelFollowers: Int,
    @SerializedName("verified_badge")
    val verifiedBadge: Boolean,
    @SerializedName("followed")
    val channelFollowed: Boolean,
)