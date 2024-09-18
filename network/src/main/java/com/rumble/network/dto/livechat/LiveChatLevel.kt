package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

data class LiveChatLevel(
    @SerializedName("price_dollars")
    val priceDollar: Double,
    @SerializedName("duration")
    val duration: Int,
    @SerializedName("colors")
    val colors: LiveChatColor,
    @SerializedName("ids")
    val idList: List<String>
)
