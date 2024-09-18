package com.rumble.domain.premium.domain.usecases

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.network.session.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ShouldShowPremiumPromoUseCase @Inject constructor(
    private val sessionManager: SessionManager,
    private val userPreferenceManager: UserPreferenceManager,
) {

    suspend operator fun invoke(): Boolean {
        return sessionManager.isPremiumUserFlow.first().not() && isOverNextDisplayTrigger()
    }

    private fun isOverNextDisplayTrigger(): Boolean {
        val promptInterval = FirebaseRemoteConfig.getInstance().getLong("premium_promo_display_delay_days")
        val lastPromoTimeStamp =
            runBlocking { userPreferenceManager.lastPremiumPromoTimeStampFlow.first() }
        val millisTrigger: Long = TimeUnit.DAYS.toMillis(promptInterval)
        return (lastPromoTimeStamp + millisTrigger) < System.currentTimeMillis()
    }
}