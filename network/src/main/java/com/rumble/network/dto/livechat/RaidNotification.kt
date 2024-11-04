package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

data class RaidNotification(
    @SerializedName("start_ts")
    val startTimestamp: Long,
)
