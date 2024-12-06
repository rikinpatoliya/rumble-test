package com.rumble.domain.common.domain.usecase

import com.appsflyer.AppsFlyerConsent
import com.appsflyer.AppsFlyerLib
import com.rumble.domain.settings.model.UserPreferenceManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SetConsentDataUseCase @Inject constructor(
    private val userPreferenceManager: UserPreferenceManager,
) {
    suspend operator fun invoke(hasConsent: Boolean) {
        val tosAccepted = userPreferenceManager.tosAcceptedFlow.first()
        if (tosAccepted.not()) {
            AppsFlyerLib.getInstance().setConsentData(
                AppsFlyerConsent.forGDPRUser(hasConsent, hasConsent)
            )
            if (hasConsent) {
                userPreferenceManager.setTosAccepted()
            }
        }
    }
}