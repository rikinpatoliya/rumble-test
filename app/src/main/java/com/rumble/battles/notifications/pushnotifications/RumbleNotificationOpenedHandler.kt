package com.rumble.battles.notifications.pushnotifications

import android.content.Context
import android.content.Intent
import androidx.media3.common.util.UnstableApi
import com.google.gson.Gson
import com.onesignal.notifications.INotificationClickEvent
import com.onesignal.notifications.INotificationClickListener
import com.rumble.analytics.PushNotificationInteractionEvent
import com.rumble.battles.landing.LandingActivity
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.notifications.domain.domainmodel.KEY_NOTIFICATION_VIDEO_DETAILS
import com.rumble.domain.notifications.domain.domainmodel.NotificationDestination
import com.rumble.domain.notifications.domain.domainmodel.RumbleNotificationData
import com.rumble.domain.notifications.domain.domainmodel.RumbleOneSignalNotificationData
import com.rumble.domain.notifications.domain.domainmodel.VideoDetailsNotificationData
import com.rumble.domain.notifications.model.NotificationDataManager
import com.rumble.network.session.SessionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import java.util.UUID
import javax.inject.Inject

private const val TAG = "RumbleNotificationOpenedHandler"

@UnstableApi
class RumbleNotificationOpenedHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val analyticsEventUseCase: AnalyticsEventUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val sessionManager: SessionManager,
    private val notificationDataManager: NotificationDataManager,
) : INotificationClickListener {

    override fun onClick(event: INotificationClickEvent) {
        analyticsEventUseCase(PushNotificationInteractionEvent)
        var notificationData = RumbleOneSignalNotificationData()
        try {
            notificationData = Gson().fromJson(
                event.notification.additionalData.toString(),
                RumbleOneSignalNotificationData::class.java
            )
        } catch (throwable: Throwable) {
            unhandledErrorUseCase(TAG, throwable)
        } finally {
            NotificationDestination.findDestination(notificationData)?.let {
                when (it) {
                    NotificationDestination.VideoDetails -> openVideoDetails(notificationData)
                    NotificationDestination.PremiumMenu -> notifyOpenPremiumMenu()
                }
            }
        }
    }

    private fun openVideoDetails(notificationData: RumbleOneSignalNotificationData) {
        runBlocking { sessionManager.allowContentLoadFlow(false) }
        context.startActivity(
            Intent(context, LandingActivity::class.java).apply {
                putExtra(
                    KEY_NOTIFICATION_VIDEO_DETAILS,
                    RumbleNotificationData(
                        guid = UUID.randomUUID().toString(),
                        videoDetailsNotificationData = VideoDetailsNotificationData(
                            notificationData.id,
                            notificationData.url
                        ),
                    )
                )
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }

    private fun notifyOpenPremiumMenu() {
        runBlocking { notificationDataManager.setShowPremiumMenu(true) }
    }
}