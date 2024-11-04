package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

data class Raid(
    @SerializedName("current_channel_name")
    val currentChannelName: String,
    @SerializedName("current_channel_avatar_url")
    val currentChannelAvatar: String?,
    @SerializedName("target_url")
    val targetUrl: String,
    @SerializedName("target_channel_name")
    val targetChannelName: String,
    @SerializedName("target_channel_avatar_url")
    val targetChannelAvatar: String?,
    @SerializedName("target_video_title")
    val targetVideoTitle: String,
    @SerializedName("start_ts")
    val startTimestamp: Long,
    @SerializedName("redirection_steps")
    val redirectionSteps: Map<Float, List<Int>>
)