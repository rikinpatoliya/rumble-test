package com.rumble.network.dto.login

import com.google.gson.annotations.SerializedName

enum class TvPairingCodeDataStatus {
    @SerializedName("success")
    SUCCESS,
    @SerializedName("failure")
    FAILURE
}