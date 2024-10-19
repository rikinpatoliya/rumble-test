package com.rumble.domain.onboarding.domain.domainmodel

sealed class OnboardingViewState

object None : OnboardingViewState()
object ShowOnboarding : OnboardingViewState()
object DoNotShow : OnboardingViewState()
data class ShowOnboardingPopups(val list: List<OnboardingPopupType>) : OnboardingViewState()