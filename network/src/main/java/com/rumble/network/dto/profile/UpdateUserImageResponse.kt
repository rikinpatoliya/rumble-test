package com.rumble.network.dto.profile

import com.google.gson.JsonPrimitive
import com.google.gson.annotations.SerializedName
import com.rumble.network.NetworkRumbleConstants.SUCCESS_STRING_RESPONSE
import com.rumble.network.dto.ErrorResponseItem

data class UpdateUserImageResponse(
    @SerializedName("return")
    private val _success: JsonPrimitive,
    @SerializedName("error")
    val error: ErrorResponseItem?,
) {
    val success: Boolean
        get() {
            return if (_success.isBoolean)
                _success.asBoolean
            else
                _success.asString.equals(SUCCESS_STRING_RESPONSE)
        }
}