package com.rumble.domain.profile.domainmodel

import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.common.model.RumbleResult

sealed class ProfileNotificationsResult {

    data class ProfileNotificationsSuccess(val profileNotifications: List<ProfileNotificationEntity>) :
        ProfileNotificationsResult()

    data class ProfileNotificationsError(override val rumbleError: RumbleError?) :
        ProfileNotificationsResult(), RumbleResult
}