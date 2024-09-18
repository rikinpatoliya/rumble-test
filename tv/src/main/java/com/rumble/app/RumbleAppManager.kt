package com.rumble.app

import com.rumble.analytics.AppLaunchEvent
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import javax.inject.Inject
import javax.inject.Singleton

interface RumbleAppHandler {
    fun onAppLaunch()
}

@Singleton
class RumbleAppManager @Inject constructor(
    val analyticsEventUseCase: AnalyticsEventUseCase,
) : RumbleAppHandler {
    override fun onAppLaunch() {
        analyticsEventUseCase(AppLaunchEvent)
    }


}