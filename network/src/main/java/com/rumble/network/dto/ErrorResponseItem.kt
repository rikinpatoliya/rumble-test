package com.rumble.network.dto

import com.google.gson.annotations.SerializedName

data class ErrorResponseItem(
    @SerializedName("code")
    val code: String,
    @SerializedName("message", alternate = ["msg"])
    val message: String,
    @SerializedName("type")
    val type: String? = null
)