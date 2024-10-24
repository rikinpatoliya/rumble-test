package com.rumble.network.dto.livevideo

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.livechat.LiveGate

data class LiveReportData(
    @SerializedName("video_id")
    val videoId: Long,
    @SerializedName("num_watching_now")
    val watchingNow: Long,
    @SerializedName("livestream_status")
    val liveStatus: Int?,
    @SerializedName("live_gate")
    val liveGate: LiveGate?,
)