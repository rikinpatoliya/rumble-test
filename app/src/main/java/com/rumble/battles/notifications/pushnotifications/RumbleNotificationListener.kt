package com.rumble.battles.notifications.pushnotifications

import com.urbanairship.push.NotificationActionButtonInfo
import com.urbanairship.push.NotificationInfo
import com.urbanairship.push.NotificationListener

object RumbleNotificationListener : NotificationListener {

    override fun onNotificationPosted(notificationInfo: NotificationInfo) {
       //TODO: to be implemented
    }

    override fun onNotificationOpened(notificationInfo: NotificationInfo): Boolean {
        //TODO: to be implemented
        return false
    }

    override fun onNotificationForegroundAction(
        notificationInfo: NotificationInfo,
        actionButtonInfo: NotificationActionButtonInfo
    ): Boolean {
        //TODO: to be implemented
        return false
    }

    override fun onNotificationBackgroundAction(
        notificationInfo: NotificationInfo,
        actionButtonInfo: NotificationActionButtonInfo
    ) {
        //TODO: to be implemented
    }

    override fun onNotificationDismissed(notificationInfo: NotificationInfo) {
        //TODO: to be implemented
    }
}