package com.rumble.network.dto.ads.rumble

import com.google.gson.annotations.SerializedName

data class BindingData(
    @SerializedName("cpm")
    val cpm: Double,
    @SerializedName("price")
    val price: String
)
