package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

data class LiveChatRant(
    @SerializedName("price_cents")
    val priceCents: Int,
    @SerializedName("expires_on")
    val expiresOn: String
)
