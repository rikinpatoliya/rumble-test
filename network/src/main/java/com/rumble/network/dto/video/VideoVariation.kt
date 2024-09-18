package com.rumble.network.dto.video

import com.google.gson.annotations.SerializedName

data class VideoVariation(
    @SerializedName("url")
    val url: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("res")
    val res: Int,
    @SerializedName("bitrate_kbps")
    val bitrate: Int,
    @SerializedName("quality_text")
    val qualityText: String,
    @SerializedName("bitrate_text")
    val bitrateText: String?
)
