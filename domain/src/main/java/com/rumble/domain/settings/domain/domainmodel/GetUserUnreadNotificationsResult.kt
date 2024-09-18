package com.rumble.domain.settings.domain.domainmodel

import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.common.model.RumbleResult

data class GetUserUnreadNotificationsResult(
    val success: Boolean,
    val hasUnreadNotifications: Boolean = false,
    override val rumbleError: RumbleError? = null,
) : RumbleResult