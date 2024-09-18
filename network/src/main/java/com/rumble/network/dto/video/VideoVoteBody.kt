package com.rumble.network.dto.video

import com.google.gson.annotations.SerializedName

data class VideoVoteBody(
    @SerializedName("data")
    val data: VideoVoteData
)

data class VideoVoteData(
    @SerializedName("video_id")
    val videoId: Long,
    @SerializedName("value")
    val vote: Int,
)