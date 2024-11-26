package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.ErrorResponseItem

data class ErrorResponse(
    @SerializedName("errors")
    val errors: List<ErrorResponseItem>
)
