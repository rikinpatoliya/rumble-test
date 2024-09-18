package com.rumble.domain.analytics.domain.usecases

import com.rumble.analytics.ContentEvent
import com.rumble.analytics.VideoPlayerImpressionEvent
import com.rumble.analytics.VideoViewImpressionEvent
import javax.inject.Inject

class LogVideoDetailsUseCase @Inject constructor(
    private val analyticsEventUseCase: AnalyticsEventUseCase
) {
    operator fun invoke(screenId: String, contentId: String) {
        analyticsEventUseCase(VideoPlayerImpressionEvent(screenId))
        analyticsEventUseCase(VideoViewImpressionEvent(screenId))
        analyticsEventUseCase(ContentEvent(contentId), true)
    }
}