package com.rumble.domain.analytics.domain.usecases

import com.rumble.analytics.CardSize
import com.rumble.analytics.RumbleVideoEvent
import com.rumble.network.api.VideoApi
import javax.inject.Inject

class LogRumbleVideoUseCase @Inject constructor(
    private val videoApi: VideoApi,
    private val analyticsEventUseCase: AnalyticsEventUseCase
) {
    suspend operator fun invoke(
        videoPath: String,
        screenId: String,
        index: Int? = null,
        cardSize: CardSize? = null,
        category: String? = null
    ) {
        videoApi.reportVideoPageView(videoPath)
        analyticsEventUseCase(RumbleVideoEvent(screenId, index, cardSize, category), true)
    }
}