package com.rumble.domain.analytics.domain.usecases

import com.rumble.analytics.CardSize
import com.rumble.analytics.VideoCardViewImpressionEvent
import com.rumble.analytics.VideoViewImpressionEvent
import javax.inject.Inject

class LogVideoCardImpressionUseCase @Inject constructor(
    private val analyticsEventUseCase: AnalyticsEventUseCase,
    private val logRumbleVideoUseCase: LogRumbleVideoUseCase,
) {
    suspend operator fun invoke(
        videoPath: String,
        screenId: String,
        index: Int,
        cardSize: CardSize,
        category: String? = null
    ) {
        analyticsEventUseCase(VideoCardViewImpressionEvent(screenId, index, cardSize, category))
        analyticsEventUseCase(VideoViewImpressionEvent(screenId, index, cardSize, category))
        if (cardSize == CardSize.REGULAR) {
            logRumbleVideoUseCase(videoPath, screenId, index, cardSize, category)
        }
    }
}