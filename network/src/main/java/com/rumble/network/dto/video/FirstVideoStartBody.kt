package com.rumble.network.dto.video

import com.google.gson.annotations.SerializedName

data class FirstVideoStartBody(
    @SerializedName("data")
    val data: FirstVideoStartBodyData
)

data class FirstVideoStartBodyData(
    @SerializedName("method")
    val method: Int,
    @SerializedName("url")
    val url: String,
    @SerializedName("tts")
    val tts: Long,
    @SerializedName("cell")
    val networkType: Int
)