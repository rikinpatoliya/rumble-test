package com.rumble.network.dto.login


data class TvPairingCodeVerificationData(
    val status : TvPairingCodeVerificationDataStatus,
    val message : String = "",
    val user : TvUser?
)
