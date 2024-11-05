package com.rumble.network.dto.login

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.ErrorResponse

data class RegisterErrorResponse(
    @SerializedName("errors")
    val errors: List<ErrorResponse>
)