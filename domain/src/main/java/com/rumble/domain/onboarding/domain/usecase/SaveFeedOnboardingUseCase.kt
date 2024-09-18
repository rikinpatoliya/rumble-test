package com.rumble.domain.onboarding.domain.usecase

import com.rumble.domain.onboarding.domain.domainmodel.OnboardingType
import com.rumble.domain.onboarding.domain.domainmodel.RoomOnboardingView
import com.rumble.domain.onboarding.model.repsitory.OnboardingViewRepository
import javax.inject.Inject

class SaveFeedOnboardingUseCase @Inject constructor(
    private val onboardingViewRepository: OnboardingViewRepository
) {
    suspend operator fun invoke(type: OnboardingType) {
        onboardingViewRepository.saveOnboarding(
            roomOnboardingView = RoomOnboardingView(
                onboardingType = type,
                version = CURRENT_ONBOARDING_VERSION
            )
        )
    }

    suspend operator fun invoke(typeList: List<OnboardingType>) {
        onboardingViewRepository.saveOnboardingList(
            typeList.map {
                RoomOnboardingView(
                    onboardingType = it,
                    version = CURRENT_ONBOARDING_VERSION
                )
            }
        )
    }
}