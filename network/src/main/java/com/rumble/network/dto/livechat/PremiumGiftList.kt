package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

data class PremiumGiftList(
    @SerializedName("type")
    val type: String,
    @SerializedName("products")
    val productList: List<PremiumGift>,
)