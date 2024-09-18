package com.rumble.network.dto.ads.rumble

import com.google.gson.annotations.SerializedName

data class RumbleAdResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("ads")
    val adList: List<RumbleAd>
)
