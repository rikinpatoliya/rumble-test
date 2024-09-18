package com.rumble.network.dto.livevideo

import com.google.gson.annotations.SerializedName

data class LiveReportBodyData(
    @SerializedName("video_id")
    val videoId: Long,
    @SerializedName("viewer_id")
    val viewerId: String
)
