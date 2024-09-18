package com.rumble.network.dto.livevideo

import com.google.gson.annotations.SerializedName

data class LiveReportData(
    @SerializedName("video_id")
    val videoId: Long,
    @SerializedName("num_watching_now")
    val watchingNow: Long,
    @SerializedName("livestream_status")
    val liveStatus: Int?
)