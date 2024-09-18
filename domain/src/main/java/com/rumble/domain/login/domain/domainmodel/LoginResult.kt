package com.rumble.domain.login.domain.domainmodel

import com.rumble.domain.common.model.RumbleError

data class LoginResult(
    val success: Boolean,
    val error: String? = null,
    val cookie: String? = null,
    val userId: Int? = null,
    val userName: String? = null,
    val userPicture: String? = null,
    val status : LoginResultStatus = LoginResultStatus.INCOMPLETE,
    val rumbleError: RumbleError? = null
)