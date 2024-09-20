package com.rumble.domain.onboarding.domain.usecase

import com.rumble.domain.onboarding.domain.domainmodel.DoNotShow
import com.rumble.domain.onboarding.domain.domainmodel.OnboardingPopupType
import com.rumble.domain.onboarding.domain.domainmodel.OnboardingType
import com.rumble.domain.onboarding.domain.domainmodel.OnboardingViewState
import com.rumble.domain.onboarding.domain.domainmodel.RoomOnboardingView
import com.rumble.domain.onboarding.domain.domainmodel.ShowLibraryOnboarding
import com.rumble.domain.onboarding.domain.domainmodel.ShowOnboardingPopups
import com.rumble.domain.onboarding.model.repsitory.OnboardingViewRepository
import com.rumble.network.session.SessionManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/* This should be incremented when we want users to see the onboarding
again in the future if we make changes */
const val CURRENT_ONBOARDING_VERSION = 0

class FeedOnboardingViewUseCase @Inject constructor(
    private val onboardingViewRepository: OnboardingViewRepository,
    private val saveFeedOnboardingUseCase: SaveFeedOnboardingUseCase,
    private val showLibraryOnboardingUseCase: ShowLibraryOnboardingUseCase,
    private val sessionManager: SessionManager
) {

    suspend operator fun invoke(versionCode: Int): OnboardingViewState {
        val oldLibraryResult = handleLibraryOnboardingTransition()
        val libraryPopupResult = onboardingViewRepository.getOnboarding(
            onboardingType = OnboardingType.YourLibrary,
            version = CURRENT_ONBOARDING_VERSION
        )
        val feedResult = handleFeedOnboardingTransition()
        val playbackResult = onboardingViewRepository.getOnboarding(
            onboardingType = OnboardingType.PlaybackSettings,
            version = CURRENT_ONBOARDING_VERSION
        )
        val onboardingList = filterOnboardingList(onboardingViewRepository.getOnboardingList())
        return if (oldLibraryResult == null && libraryPopupResult == null && showLibraryOnboardingUseCase(versionCode)) {
            ShowLibraryOnboarding
        } else if (feedResult == null || playbackResult == null || onboardingList.isNotEmpty()) {
            ShowOnboardingPopups(onboardingList)
        } else {
            DoNotShow
        }
    }

    private suspend fun filterOnboardingList(onboardingList: List<RoomOnboardingView>): List<OnboardingPopupType> {
        val userId = sessionManager.userIdFlow.first()
        val result = mutableListOf<OnboardingPopupType>()
        val onboardingTypeList = onboardingList.map {
            it.onboardingType
        }
        if (onboardingTypeList.contains(OnboardingType.SearchRumble).not())
            result.add(OnboardingPopupType.SearchRumble)
        if (onboardingTypeList.contains(OnboardingType.DiscoverContent).not())
            result.add(OnboardingPopupType.DiscoverContent)
        if (onboardingTypeList.contains(OnboardingType.FollowingChannels).not() && userId.isNotEmpty())
            result.add(OnboardingPopupType.FollowingChannels)
        if (onboardingTypeList.contains(OnboardingType.YourLibrary).not() && userId.isNotEmpty())
            result.add(OnboardingPopupType.YourLibrary)
        return result
    }

    private suspend fun handleLibraryOnboardingTransition(): RoomOnboardingView? {
        val result = onboardingViewRepository.getOnboarding(
            onboardingType = OnboardingType.LibraryScreen,
            version = CURRENT_ONBOARDING_VERSION
        )
        if (result != null) {
            if (onboardingViewRepository.getOnboarding(
                    onboardingType = OnboardingType.YourLibrary,
                    version = CURRENT_ONBOARDING_VERSION
                ) == null
            ) saveFeedOnboardingUseCase(OnboardingType.YourLibrary)
        }
        return result
    }

    private suspend fun handleFeedOnboardingTransition(): RoomOnboardingView? {
        val result = onboardingViewRepository.getOnboarding(
            onboardingType = OnboardingType.FeedScreen,
            version = CURRENT_ONBOARDING_VERSION
        )
        if (result != null) {
            if (onboardingViewRepository.getOnboarding(
                    onboardingType = OnboardingType.SearchRumble,
                    version = CURRENT_ONBOARDING_VERSION
                ) == null
            ) saveFeedOnboardingUseCase(OnboardingType.SearchRumble)
            if (onboardingViewRepository.getOnboarding(
                    onboardingType = OnboardingType.DiscoverContent,
                    version = CURRENT_ONBOARDING_VERSION
                ) == null
            ) saveFeedOnboardingUseCase(OnboardingType.DiscoverContent)
        }
        return result
    }
}