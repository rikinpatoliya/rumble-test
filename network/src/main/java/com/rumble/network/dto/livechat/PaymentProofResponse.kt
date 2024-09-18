package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

data class PaymentProofResponse(
    @SerializedName("data")
    val paymentProofData: PaymentProofResponseData
)

data class PaymentProofResponseData(
    @SerializedName("id")
    val pendingMessageId: String,
    @SerializedName("user")
    val userData: PaymentProofUserData
)

data class PaymentProofUserData(
    @SerializedName("id")
    val id: String,
    @SerializedName("username")
    val userName: String,
    @SerializedName("color")
    val color: String,
    @SerializedName("badges")
    val badges: List<String>
)
