package com.rumble.network.dto.login

import com.google.gson.annotations.SerializedName

enum class TvPairingCodeVerificationDataStatus {

    /**
     * When the code has been successfully paired
     */
    @SerializedName("success")
    SUCCESS,

    /**
     * Currently, the following error messages can be observed:
     * - Registration code format not valid — likely indicates a client bug
     * - Code doesn't exist
     * - Code expired
     *
     * In the latter two cases, the client should request a new code using roku.link.code_get.
     * Currently, there are no special codes that can be used to distinguish those cases from
     * others, so the client has to parse the textual content of the "message" field.
     */
    @SerializedName("failure")
    FAILURE,

    /**
     * When the code has not yet been paired
     */
    @SerializedName("incomplete")
    INCOMPLETE
}