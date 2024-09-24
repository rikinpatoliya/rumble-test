package com.rumble.domain.landing.usecases

import com.onesignal.OneSignal
import com.rumble.domain.landing.domainmodel.UserTag
import com.rumble.domain.landing.domainmodel.UserTagValue
import com.rumble.domain.premium.domain.usecases.FetchUserInfoUseCase
import com.rumble.network.session.SessionManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SetOneSignalUserPremiumTagUseCase @Inject constructor(
    private val sessionManager: SessionManager,
    private val fetchUserInfoUseCase: FetchUserInfoUseCase,
) {
    suspend operator fun invoke(loggedIn: Boolean) {
        if (loggedIn) {
            fetchUserInfoUseCase()
            if (sessionManager.isPremiumUserFlow.first()) {
                OneSignal.User.addTag(UserTag.Premium.value, UserTagValue.True.value)
            } else {
                OneSignal.User.addTag(UserTag.Premium.value, UserTagValue.False.value)
            }
        } else {
            OneSignal.User.addTag(UserTag.Premium.value, UserTagValue.NonApplicable.value)
        }
    }
}