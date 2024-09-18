package com.rumble.network.dto.supscription

import com.google.gson.annotations.SerializedName

data class SubscriptionResponse(
    @SerializedName("data")
    val subscriptionData: SubscriptionData?,
    @SerializedName("errors")
    val subscriptionErrors: List<SubscriptionError>?
)

data class SubscriptionData(
    @SerializedName("success")
    val success: Boolean
)

data class SubscriptionError(
    @SerializedName("code")
    val code: String,
    @SerializedName("message")
    val errorMessage: String,
    @SerializedName("type")
    val type: String
)
