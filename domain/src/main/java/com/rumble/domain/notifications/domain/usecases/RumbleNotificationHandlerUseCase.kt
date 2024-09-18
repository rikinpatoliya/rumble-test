package com.rumble.domain.notifications.domain.usecases

import androidx.core.text.isDigitsOnly
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.feed.domain.usecase.GetVideoDetailsUseCase
import com.rumble.domain.notifications.domain.domainmodel.NotificationHandlerResult
import com.rumble.domain.notifications.domain.domainmodel.RumbleNotificationData
import com.rumble.domain.notifications.domain.domainmodel.VideoDetailsNotificationData
import javax.inject.Inject

private const val TAG = "RumbleNotificationHandlerUseCase"

class RumbleNotificationHandlerUseCase @Inject constructor(
    private val getVideoDetailsUseCase: GetVideoDetailsUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase
) {

    suspend operator fun invoke(rumbleNotificationData: RumbleNotificationData): NotificationHandlerResult {
        return when {
            rumbleNotificationData.videoDetailsNotificationData != null -> getVideoDetailsNotificationData(
                rumbleNotificationData.videoDetailsNotificationData
            )
            else -> NotificationHandlerResult.UnhandledNotificationData
        }
    }

    private suspend fun getVideoDetailsNotificationData(videoDetailsNotificationData: VideoDetailsNotificationData): NotificationHandlerResult {
        return if (!videoDetailsNotificationData.id.isNullOrEmpty()) {
            try {
                val videoEntity =
                    getVideoDetailsUseCase(videoDetailsNotificationData.id.toLong())
                NotificationHandlerResult.VideoDetailsNotificationData(
                    success = videoEntity != null,
                    videoEntity = videoEntity
                )
            } catch (e: Exception) {
                unhandledErrorUseCase(TAG, e)
                NotificationHandlerResult.UnhandledNotificationData
            }
        } else if (!videoDetailsNotificationData.url.isNullOrEmpty()
            && !videoDetailsNotificationData.url.isDigitsOnly()
        ) {
            val videoEntity =
                getVideoDetailsUseCase(videoDetailsNotificationData.url)
            NotificationHandlerResult.VideoDetailsNotificationData(
                success = videoEntity != null,
                videoEntity = videoEntity
            )
        } else {
            NotificationHandlerResult.UnhandledNotificationData
        }
    }
}