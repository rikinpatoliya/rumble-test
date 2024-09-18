package com.rumble.videoplayer.player.internal.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class RumbleNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        const val NOTIFICATION_ID = 1000
        private const val NOTIFICATION_CHANNEL_NAME = "Rumble notification channel"
        private const val NOTIFICATION_CHANNEL_ID = "Rumble notification channel id"
    }

    private var notificationManager: NotificationManagerCompat =
        NotificationManagerCompat.from(context)
    private var playerNotificationManager: PlayerNotificationManager? = null

    init {
        createNotificationChannel()
    }

    @UnstableApi
    fun getNotification(notificationData: NotificationData): Notification {
        buildNotification(notificationData)
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID).build()
    }

    @UnstableApi
    fun stopNotification() = playerNotificationManager?.setPlayer(null)

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    @UnstableApi
    private fun buildNotification(notificationData: NotificationData) {
        playerNotificationManager = PlayerNotificationManager.Builder(context, NOTIFICATION_ID, NOTIFICATION_CHANNEL_ID)
            .setMediaDescriptionAdapter(MediaNotificationAdapter(context = context))
            .build()
            .also {
                it.setUseNextAction(false)
                it.setUseNextActionInCompactView(false)
                it.setUsePreviousAction(false)
                it.setUsePreviousActionInCompactView(false)
                it.setUseFastForwardAction(notificationData.enableSeekBar)
                it.setUseFastForwardActionInCompactView(notificationData.enableSeekBar)
                it.setUseRewindAction(notificationData.enableSeekBar)
                it.setUseRewindActionInCompactView(notificationData.enableSeekBar)
                it.setMediaSessionToken(notificationData.mediaSession.sessionCompatToken)
                it.setPriority(NotificationCompat.PRIORITY_LOW)
                it.setPlayer(notificationData.player)
            }
    }
}