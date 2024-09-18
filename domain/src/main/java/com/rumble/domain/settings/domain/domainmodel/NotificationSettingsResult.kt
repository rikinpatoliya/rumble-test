package com.rumble.domain.settings.domain.domainmodel

data class NotificationSettingsResult(
    val success: Boolean,
    val error: String?,
    val canUseCustomApiDomain: Boolean,
    val notificationSettingsEntity: NotificationSettingsEntity?,
)