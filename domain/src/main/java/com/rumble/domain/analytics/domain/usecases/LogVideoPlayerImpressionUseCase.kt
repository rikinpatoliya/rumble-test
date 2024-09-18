package com.rumble.domain.analytics.domain.usecases

import com.rumble.analytics.CardSize
import com.rumble.analytics.VideoPlayerImpressionEvent
import com.rumble.analytics.VideoViewImpressionEvent
import javax.inject.Inject

class LogVideoPlayerImpressionUseCase @Inject constructor(
    private val analyticsEventUseCase: AnalyticsEventUseCase
) {
    operator fun invoke(
        screenId: String,
        index: Int? = null,
        cardSize: CardSize? = null,
        category: String? = null
    ) {
        analyticsEventUseCase(VideoPlayerImpressionEvent(screenId, index, cardSize, category))
        analyticsEventUseCase(VideoViewImpressionEvent(screenId, index, cardSize, category))
    }
}