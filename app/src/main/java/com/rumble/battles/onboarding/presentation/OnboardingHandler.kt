package com.rumble.battles.onboarding.presentation

import androidx.compose.ui.geometry.Offset
import com.rumble.domain.onboarding.domain.domainmodel.OnboardingPopupType
import com.rumble.domain.onboarding.domain.domainmodel.OnboardingViewState
import kotlinx.coroutines.flow.StateFlow

interface OnboardingHandler {
    val onboardingViewState: StateFlow<OnboardingViewState>
    val popupsListIndex: StateFlow<Int>
    val discoverIconLocationState: StateFlow<Offset>
    val searchIconLocationState: StateFlow<Offset>
    val followingIconLocationState: StateFlow<Offset>
    val libraryIconLocationState: StateFlow<Offset>

    fun onLibrary(withNavigation: Boolean = false)
    fun onSkipAll(popupsList: List<OnboardingPopupType>)
    fun onNext(popup: OnboardingPopupType, index: Int, popupsList: List<OnboardingPopupType>)
    fun onBack(index: Int)
    fun onSearchIconMeasured(center: Offset)
    fun onDiscoverIconMeasured(center: Offset)
    fun onFollowingIconMeasured(center: Offset)
    fun onLibraryIconMeasured(center: Offset)
}