package com.rumble.domain.notifications.domain.domainmodel

enum class NotificationDestination(val value: String) {
    VideoDetails("video"),
    PremiumMenu("premium");

    companion object {
        fun findDestination(data: RumbleOneSignalNotificationData): NotificationDestination? {
           return data.url?.let {
               VideoDetails
            } ?: run {
                values().find { it.value == data.destination }
            }
        }
    }
}