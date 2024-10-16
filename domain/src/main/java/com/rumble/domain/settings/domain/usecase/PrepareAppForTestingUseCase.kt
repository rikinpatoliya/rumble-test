package com.rumble.domain.settings.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.login.domain.usecases.RumbleLoginUseCase
import com.rumble.domain.onboarding.domain.domainmodel.OnboardingType
import com.rumble.domain.onboarding.domain.usecase.SaveFeedOnboardingUseCase
import com.rumble.domain.profile.domain.SignOutUseCase
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
    private val loginUseCase: RumbleLoginUseCase,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
) : RumbleUseCase {

    suspend operator fun invoke(uitUserName: String?, uitPassword: String?) {
        saveFeedOnboardingUseCase(OnboardingType.values().toList())
        updateSubdomainUseCase(TESTING_SUBDOMAIN)
        userPreferenceManager.saveLastPremiumPromoTimeStamp(System.currentTimeMillis())
        userPreferenceManager.saveUitTestingMode(true)
        sessionManager.saveLastLoginPromptTime(
            LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        )
        signOutUseCase()
        if (uitUserName != null && uitPassword != null)
            loginUseCase(uitUserName, uitPassword)
    }
}