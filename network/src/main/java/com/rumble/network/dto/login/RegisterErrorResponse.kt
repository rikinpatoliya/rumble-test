package com.rumble.network.dto.login

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.ErrorResponseItem

data class RegisterErrorResponse(
    @SerializedName("errors")
    val errors: List<ErrorResponseItem>
)