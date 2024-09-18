package com.rumble.domain.common.domain.domainmodel

import com.rumble.domain.common.model.RumbleError
import com.rumble.network.dto.login.TvPairingCodeData

sealed class TvPairingCodeResult {
    data class Success(val tvPairingCodeData: TvPairingCodeData) : TvPairingCodeResult()
    data class Failure(val rumbleError: RumbleError) : TvPairingCodeResult()
}