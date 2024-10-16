package com.rumble.network.dto.supscription

import com.google.gson.annotations.SerializedName

data class SubscriptionBody(
    @SerializedName("data")
    val data: SubscriptionBodyData
)

data class SubscriptionBodyData(
    @SerializedName("platform")
    val platform: String = "android",
    @SerializedName("product_id")
    val productId: String,
    @SerializedName("purchase_token")
    val purchaseToken: String,
    @SerializedName("package_name")
    val packageName: String,
    @SerializedName("iid")
    val installationId: String,
    @SerializedName("video_id")
    val videoId: Long?,
    @SerializedName("source")
    val source: String?
)