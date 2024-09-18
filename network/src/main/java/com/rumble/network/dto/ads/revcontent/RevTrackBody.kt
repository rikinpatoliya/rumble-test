package com.rumble.network.dto.ads.revcontent

import com.google.gson.annotations.SerializedName

data class RevTrackBody(
    @SerializedName("view")
    val view: String,
    @SerializedName("view_type")
    val viewType: String = "",
    @SerializedName("p[]")
    val position: String = ""
)
