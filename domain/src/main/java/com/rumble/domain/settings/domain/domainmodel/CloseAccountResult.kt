package com.rumble.domain.settings.domain.domainmodel

import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.common.model.RumbleResult

data class CloseAccountResult(
    val success: Boolean,
    override val rumbleError: RumbleError?,
) : RumbleResult