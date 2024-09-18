package com.rumble.domain.profile.domain

import com.rumble.domain.landing.usecases.AppsFlySetUserIdUseCase
import com.rumble.domain.landing.usecases.OneSignalPushNotificationEnableUseCase
import com.rumble.domain.landing.usecases.SetOneSignalUserPremiumTagUseCase
import com.rumble.domain.profile.model.repository.ProfileRepository
import com.rumble.network.session.SessionManager
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val sessionManager: SessionManager,
    private val oneSignalPushNotificationEnableUseCase: OneSignalPushNotificationEnableUseCase,
    private val setOneSignalUserPremiumTagUseCase: SetOneSignalUserPremiumTagUseCase,
    private val appsFlySetUserIdUseCase: AppsFlySetUserIdUseCase,
) {
    suspend operator fun invoke(
        withUnsubscribeFromPushNotifications: Boolean = false,
        oemConfig: () -> Unit = {}
    ) {
        if (withUnsubscribeFromPushNotifications) {
            oneSignalPushNotificationEnableUseCase(false)
        }
        sessionManager.clearUserData()
        profileRepository.signOut()
        setOneSignalUserPremiumTagUseCase(false)
        appsFlySetUserIdUseCase("")
        oemConfig()
    }
}