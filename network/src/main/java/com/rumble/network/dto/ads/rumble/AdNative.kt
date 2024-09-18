package com.rumble.network.dto.ads.rumble

import com.google.gson.annotations.SerializedName

data class AdNative(
    @SerializedName("title")
    val title: String,
    @SerializedName("brand")
    val brand: String
)
