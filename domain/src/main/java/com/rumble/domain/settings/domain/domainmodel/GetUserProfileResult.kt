package com.rumble.domain.settings.domain.domainmodel

import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.common.model.RumbleResult
import com.rumble.domain.profile.domainmodel.UserProfileEntity

data class GetUserProfileResult(
    val success: Boolean,
    val userProfileEntity: UserProfileEntity? = null,
    override val rumbleError: RumbleError? = null,
) : RumbleResult