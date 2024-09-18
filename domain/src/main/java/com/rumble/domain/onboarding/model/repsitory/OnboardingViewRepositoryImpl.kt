package com.rumble.domain.onboarding.model.repsitory

import com.rumble.domain.onboarding.domain.domainmodel.OnboardingType
import com.rumble.domain.onboarding.domain.domainmodel.RoomOnboardingView
import com.rumble.domain.onboarding.model.datasource.local.OnboardingViewDao
import javax.inject.Inject

class OnboardingViewRepositoryImpl @Inject constructor(
    private val onboardingViewDao: OnboardingViewDao
) : OnboardingViewRepository {
    override suspend fun saveOnboarding(roomOnboardingView: RoomOnboardingView) {
        onboardingViewDao.save(roomOnboardingView = roomOnboardingView)
    }

    override suspend fun saveOnboardingList(roomOnboardingViewList: List<RoomOnboardingView>) {
        onboardingViewDao.saveList(roomOnboardingViewList = roomOnboardingViewList)
    }

    override suspend fun getOnboarding(
        onboardingType: OnboardingType,
        version: Int
    ): RoomOnboardingView? = onboardingViewDao.get(onboardingType, version)

    override suspend fun getOnboardingList(): List<RoomOnboardingView> =
        onboardingViewDao.getAll()
}