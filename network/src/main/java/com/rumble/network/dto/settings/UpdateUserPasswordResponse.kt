package com.rumble.network.dto.settings

import com.google.gson.JsonPrimitive
import com.google.gson.annotations.SerializedName
import com.rumble.network.NetworkRumbleConstants.PASSWORD_UPDATE_SUCCESS_MESSAGE
import com.rumble.network.dto.ErrorResponse

data class UpdateUserPasswordResponse(
    @SerializedName("return")
    private val _success: JsonPrimitive?,
    @SerializedName("error")
    val error: ErrorResponse?
) {
    val success: Boolean
        get() {
            return if (_success == null || _success.isBoolean)
                false
            else
                _success.asString.equals(PASSWORD_UPDATE_SUCCESS_MESSAGE)
        }
    val message: String?
        get() {
            return if (_success != null && _success.isString)
                _success.asString
            else
                error?.message
        }
}
