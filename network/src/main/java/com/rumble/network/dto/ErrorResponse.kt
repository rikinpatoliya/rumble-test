package com.rumble.network.dto

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("code")
    val code: Int? = null,
    @SerializedName("message", alternate = ["msg"])
    val message: String,
    @SerializedName("type")
    val type: String? = null
)