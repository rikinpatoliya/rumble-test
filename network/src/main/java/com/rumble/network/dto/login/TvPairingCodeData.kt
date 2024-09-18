package com.rumble.network.dto.login


data class TvPairingCodeData(
    val status : TvPairingCodeDataStatus = TvPairingCodeDataStatus.FAILURE,
    val regCode: String =  "",      // Registration code that should be presented in the UI.
    val retryInterval : Int = 1,
    val retryDuration : Long = 0,   // For how long the code is considered valid before it expires, in seconds.
                                    // When this time is running out, the client should request another code
                                    // slightly in advance.
    val message : String = "",      // Error message
    val creationTime: Long = System.currentTimeMillis() // Time when the response instance was created
)


