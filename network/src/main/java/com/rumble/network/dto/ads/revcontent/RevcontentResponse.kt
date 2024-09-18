package com.rumble.network.dto.ads.revcontent

import com.google.gson.annotations.SerializedName

data class RevcontentResponse(
    @SerializedName("content")
    val content: List<Revcontent>,
    @SerializedName("impression")
    val impression: String,
    @SerializedName("view")
    val view: String
)
