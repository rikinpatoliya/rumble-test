package com.rumble.network.dto.ads.revcontent

import com.google.gson.annotations.SerializedName

data class Revcontent(
    @SerializedName("headline")
    val headline: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("brand")
    val brand: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("uid")
    val uid: String,
    @SerializedName("target_url")
    val targetUrl: String,
    @SerializedName("impression")
    val impression: String
)
