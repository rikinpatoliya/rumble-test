package com.rumble.domain.profile.domainmodel

data class AppVersionEntity(
    val versionString: String,
    val visibility: AppVersionVisibility
)

enum class AppVersionVisibility {
    Visible,
    Hidden
}