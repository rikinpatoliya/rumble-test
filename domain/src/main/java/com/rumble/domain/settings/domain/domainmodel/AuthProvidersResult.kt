package com.rumble.domain.settings.domain.domainmodel

import com.rumble.domain.login.domain.domainmodel.LoginType

data class AuthProvidersResult(
    val success: Boolean,
    val authProviderEntity: AuthProviderEntity,
)

data class AuthProviderEntity(
    val loginType: LoginType,
    val canUnlink: Boolean,
)