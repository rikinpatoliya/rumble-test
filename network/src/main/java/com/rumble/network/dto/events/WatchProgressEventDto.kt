package com.rumble.network.dto.events

import com.google.gson.annotations.SerializedName

data class WatchProgressEventDto(
    @SerializedName("i")
    val videoId: Long,
    @SerializedName("s")
    val startPosition: Long?,
    @SerializedName("d")
    val duration: Long,
    @SerializedName("r")
    val playbackRate: Int?,
    @SerializedName("v")
    val playbackVolume: Int?,
    @SerializedName("m")
    val isMuted: Int?,
    @SerializedName("p")
    val playerType: String?,
    @SerializedName("f")
    val fullScreenModel: Int?
)
