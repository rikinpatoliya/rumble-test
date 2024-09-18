package com.rumble.network.subdomain

import com.rumble.battles.network.BuildConfig

data class RumbleSubdomain(
    val environmentSubdomain: String = BuildConfig.DEFAULT_SUBDOMAIN,
    val appSubdomain: String? = null,
    val userInitiatedSubdomain: String? = null,
    val canResetSubdomain: Boolean = false
)
