package com.rumble.network.dto.profile

import com.google.gson.annotations.SerializedName

data class UpdateProfileResponse(
    @SerializedName("success")
    val success: Boolean = false,
    @SerializedName("error")
    val error: String? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("requires_confirmation")
    val requiresConfirmation: Boolean = false,

    @SerializedName("fullname")
    val fullNameErrorMessage: String?,
    @SerializedName("city")
    val cityErrorMessage: String?,
    @SerializedName("stateprov")
    val stateErrorMessage: String?,
    @SerializedName("birthday")
    val birthdayErrorMessage:String?,
    @SerializedName("postalcode")
    val postalCodeErrorMessage: String?,
)