package com.rumble.network.dto.purchase

import com.google.gson.annotations.SerializedName

data class PurchaseResponse(
    @SerializedName("data")
    val purchaseData: PurchaseData?,
    @SerializedName("errors")
    val purchaseErrors: List<PurchaseError>?
)

data class PurchaseData(
    @SerializedName("success")
    val success: Boolean
)

data class PurchaseError(
    @SerializedName("code")
    val code: String,
    @SerializedName("message")
    val errorMessage: String,
    @SerializedName("type")
    val type: String
)
