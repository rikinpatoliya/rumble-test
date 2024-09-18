package com.rumble.network.dto.timerange

import com.google.gson.annotations.SerializedName

data class TimeRangeDto(
    @SerializedName("video_id")
    val videoId: Long,
    @SerializedName("start")
    val startTime: Float?,
    @SerializedName("duration")
    val duration: Float,
    @SerializedName("is_placeholder")
    val isPlaceHolder: Boolean
)
