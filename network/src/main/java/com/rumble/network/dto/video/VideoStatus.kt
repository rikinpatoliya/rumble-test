package com.rumble.network.dto.video

import com.google.gson.annotations.SerializedName

data class VideoStatus(
    @SerializedName("revenue")
    val revenue: Double?
)
