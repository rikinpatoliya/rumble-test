package com.rumble.domain.premium.domain.usecases

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.rumble.domain.settings.domain.usecase.IsCurrentTimeStampOverTriggerUseCase
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.network.NetworkRumbleConstants.FIREBASE_CONFIG_PREMIUM_PROMO_DISPLAY_DELAY_DAYS
import com.rumble.network.session.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ShouldShowPremiumPromoUseCase @Inject constructor(
    private val sessionManager: SessionManager,
    private val userPreferenceManager: UserPreferenceManager,
    private val isCurrentTimeStampOverTriggerUseCase: IsCurrentTimeStampOverTriggerUseCase
) {

    suspend operator fun invoke(): Boolean {
        return sessionManager.isPremiumUserFlow.first().not()
                && isCurrentTimeStampOverTriggerUseCase(
            lastTimeStamp = runBlocking { userPreferenceManager.lastPremiumPromoTimeStampFlow.first() },
            triggerMillis = TimeUnit.DAYS.toMillis(
                FirebaseRemoteConfig.getInstance().getLong(FIREBASE_CONFIG_PREMIUM_PROMO_DISPLAY_DELAY_DAYS)
            )
        )
    }
}