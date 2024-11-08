package com.rumble.domain.landing.usecases

import com.rumble.analytics.AnalyticsManager
import com.rumble.analytics.IIDUserProperty
import com.rumble.analytics.SignedInUserProperty
import com.rumble.analytics.UIDUserProperty
import com.rumble.network.di.AppFlyerId
import javax.inject.Inject

class SetUserPropertiesUseCase @Inject constructor(
    @AppFlyerId val appsFlyerId: String,
    private val analyticsManager: AnalyticsManager
) {

    operator fun invoke(userId: String?) {
        analyticsManager.setUserProperty(UIDUserProperty(userId))
        analyticsManager.setUserProperty(IIDUserProperty(appsFlyerId))
        analyticsManager.setUserProperty(SignedInUserProperty(userId != null))
    }
}