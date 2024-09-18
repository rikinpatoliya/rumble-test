package com.rumble.domain.onboarding.model.repsitory

import com.rumble.domain.onboarding.domain.domainmodel.OnboardingType
import com.rumble.domain.onboarding.domain.domainmodel.RoomOnboardingView

interface OnboardingViewRepository {
    suspend fun saveOnboarding(roomOnboardingView: RoomOnboardingView)
    suspend fun saveOnboardingList(roomOnboardingViewList: List<RoomOnboardingView>)
    suspend fun getOnboarding(onboardingType: OnboardingType, version: Int): RoomOnboardingView?
    suspend fun getOnboardingList(): List<RoomOnboardingView>
}