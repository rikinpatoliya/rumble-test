package com.rumble.domain.settings.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.onboarding.domain.domainmodel.OnboardingType
import com.rumble.domain.onboarding.domain.usecase.SaveFeedOnboardingUseCase
import com.rumble.domain.profile.domain.SignOutUseCase
import com.rumble.domain.settings.domain.domainmodel.PlaybackInFeedsMode
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.network.session.SessionManager
import com.rumble.utils.RumbleConstants.TESTING_SUBDOMAIN
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject

class PrepareAppForTestingUseCase @Inject constructor(
    private val updateSubdomainUseCase: UpdateSubdomainUseCase,
    private val saveFeedOnboardingUseCase: SaveFeedOnboardingUseCase,
    private val userPreferenceManager: UserPreferenceManager,
    private val sessionManager: SessionManager,
    private val signOutUseCase: SignOutUseCase,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
) : RumbleUseCase {

    suspend operator fun invoke(
        uitUserName: String?,
        uitPassword: String?,
        uitShowAuthLanding: String?
    ) {
        saveFeedOnboardingUseCase(OnboardingType.values().toList())
        updateSubdomainUseCase(TESTING_SUBDOMAIN)
        userPreferenceManager.saveLastPremiumPromoTimeStamp(
            LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        )
        userPreferenceManager.savePlaybackInFeedsMode(PlaybackInFeedsMode.OFF)
        userPreferenceManager.saveUitTestingMode(true)
        if (uitShowAuthLanding.isNullOrEmpty()) {
            sessionManager.saveLastLoginPromptTime(
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            )
        }
        if (uitUserName != null && uitPassword != null) {
            if (uitUserName.isNotEmpty() && uitPassword.isNotEmpty()) {
                sessionManager.saveUserName(uitUserName)
                sessionManager.savePassword(uitPassword)
            } else {
                signOutUseCase()
            }
        }
    }
}