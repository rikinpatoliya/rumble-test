package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.ErrorResponse

data class LiveChatErrorResponse(
    @SerializedName("errors")
    val errors: List<ErrorResponse>
)
