package com.rumble.domain.settings.domain.domainmodel

import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.common.model.RumbleResult

data class UpdateUserDetailsResult(
    val success: Boolean,
    val message: String? = null,
    override val rumbleError: RumbleError? = null,
) : RumbleResult