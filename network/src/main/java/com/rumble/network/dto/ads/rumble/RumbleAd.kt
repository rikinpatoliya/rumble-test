package com.rumble.network.dto.ads.rumble

import com.google.gson.annotations.SerializedName

data class RumbleAd(
    @SerializedName("type")
    val type: Int,
    @SerializedName("impression")
    val impressionUrl: String,
    @SerializedName("click")
    val clickUrl: String,
    @SerializedName("asset")
    val assetUrl: String,
    @SerializedName("expires")
    val expiration: Int,
    @SerializedName("native")
    val native: AdNative? = null,
    @SerializedName("bidding")
    val bidding: BindingData? = null
)
