package com.rumble.domain.analytics.domain.usecases

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.rumble.domain.report.domain.VideoReportConfig
import javax.inject.Inject

class ProvideVideoReportConfigUseCase @Inject constructor() {
    operator fun invoke(): VideoReportConfig {
        val configVisibility = FirebaseRemoteConfig.getInstance().getDouble("view_event_visible_area").toFloat()
        val configDelay = FirebaseRemoteConfig.getInstance().getLong("view_event_display_time_ms")
        return VideoReportConfig(
            visibilityPercentage = configVisibility,
            delayBeforeReport = configDelay
        )
    }
}