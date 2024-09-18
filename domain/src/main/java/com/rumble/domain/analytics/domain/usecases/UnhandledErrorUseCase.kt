package com.rumble.domain.analytics.domain.usecases

import com.rumble.analytics.AnalyticsManager
import javax.inject.Inject

class UnhandledErrorUseCase @Inject constructor(
    private val analyticsManager: AnalyticsManager
) {

    operator fun invoke(tag: String, throwable: Throwable) =
        analyticsManager.sendUnhandledErrorReport(tag = tag, throwable = throwable)
}