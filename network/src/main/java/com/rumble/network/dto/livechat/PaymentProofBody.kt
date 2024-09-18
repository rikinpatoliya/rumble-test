package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

data class PaymentProofBody(
    @SerializedName("data")
    val paymentProofData: PaymentProofData
)

data class PaymentProofData(
    @SerializedName("request_id")
    val requestId: String,
    @SerializedName("purchase_token")
    val purchaseToken: String,
    @SerializedName("platform")
    val platform: String = androidPlatform,
    @SerializedName("package_name")
    val packageName: String,
    @SerializedName("iid")
    val installationId: String,
)
