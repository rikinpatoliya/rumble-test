package com.rumble.domain.analytics.domain.usecases

import com.rumble.analytics.AnalyticsManager
import com.rumble.analytics.MediaErrorData
import javax.inject.Inject

class SendMediaErrorReportUseCase @Inject constructor(
    private val analyticsManager: AnalyticsManager
) {
    operator fun invoke(mediaErrorData: MediaErrorData) =
        analyticsManager.sendMediaErrorReport(mediaErrorData)
}