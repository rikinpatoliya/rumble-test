package com.rumble.domain.profile.domain

import com.rumble.domain.landing.usecases.AppsFlySetUserIdUseCase
import com.rumble.domain.landing.usecases.OneSignalLogoutUseCase
import com.rumble.domain.landing.usecases.SetOneSignalUserTagsUseCase
import com.rumble.domain.landing.usecases.SetUserPropertiesUseCase
import com.rumble.domain.livechat.model.repository.RecentEmoteRepository
import com.rumble.domain.profile.model.repository.ProfileRepository
import com.rumble.network.session.SessionManager
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val sessionManager: SessionManager,
    private val oneSignalLogoutUseCase: OneSignalLogoutUseCase,
    private val setOneSignalUserTagsUseCase: SetOneSignalUserTagsUseCase,
    private val appsFlySetUserIdUseCase: AppsFlySetUserIdUseCase,
    private val setUserPropertiesUseCase: SetUserPropertiesUseCase,
    private val recentEmoteRepository: RecentEmoteRepository,
) {
    suspend operator fun invoke(
        isTV: Boolean = false,
        oemConfig: () -> Unit = {}
    ) {
        if (isTV.not()) {
            oneSignalLogoutUseCase()
            setOneSignalUserTagsUseCase(false)
        }
        recentEmoteRepository.deleteAllRecentEmotes()
        sessionManager.clearUserData()
        profileRepository.signOut()
        appsFlySetUserIdUseCase("")
        setUserPropertiesUseCase(null)
        oemConfig()
    }
}