package com.rumble.network.dto.video

import com.google.gson.annotations.SerializedName
import java.util.*

data class Ppv(
    @SerializedName("price_cents")
    val priceCents: Int,
    @SerializedName("is_purchased")
    val isPurchased: Boolean,
    @SerializedName("purchase_deadline")
    val purchaseDeadline: String?,
    @SerializedName("product_id")
    val productId: String,
)
