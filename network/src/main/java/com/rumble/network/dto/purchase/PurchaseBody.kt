package com.rumble.network.dto.purchase

import com.google.gson.annotations.SerializedName

data class PurchaseBody(
    @SerializedName("data")
    val data: PurchaseBodyData
)

data class PurchaseBodyData(
    @SerializedName("platform")
    val platform: String = "android",
    @SerializedName("product_id")
    val productId: String,
    @SerializedName("purchase_token")
    val purchaseToken: String,
    @SerializedName("package_name")
    val packageName: String,
    @SerializedName("video_id")
    val videoId: Long,
    @SerializedName("channel_id")
    val channelId: Long?,
)