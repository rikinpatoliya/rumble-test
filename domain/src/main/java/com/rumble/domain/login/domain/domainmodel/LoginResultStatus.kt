package com.rumble.domain.login.domain.domainmodel

import com.rumble.network.dto.login.TvPairingCodeVerificationDataStatus


enum class LoginResultStatus(val status : String) {
    SUCCESS("success"),
    FAILURE("failure"),
    INCOMPLETE("incomplete");

    companion object {
        fun get(status : TvPairingCodeVerificationDataStatus): LoginResultStatus =
            when (status) {
                TvPairingCodeVerificationDataStatus.SUCCESS-> LoginResultStatus.SUCCESS
                TvPairingCodeVerificationDataStatus.FAILURE -> LoginResultStatus.FAILURE
                TvPairingCodeVerificationDataStatus.INCOMPLETE -> LoginResultStatus.INCOMPLETE
                else -> LoginResultStatus.FAILURE
            }
    }
}