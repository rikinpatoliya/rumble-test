package com.rumble.domain.onboarding.domain.domainmodel

import com.rumble.domain.R

enum class OnboardingPopupType(val titleId: Int, val descriptionId: Int, val onboardingType: OnboardingType) {
    SearchRumble(
        titleId = R.string.search_rumble,
        descriptionId = R.string.search_rumble_description,
        onboardingType = OnboardingType.SearchRumble
    ),
    DiscoverContent(
        titleId = R.string.discover_content,
        descriptionId = R.string.discover_content_description,
        onboardingType = OnboardingType.DiscoverContent
    ),
    FollowingChannels(
        titleId = R.string.following_channels,
        descriptionId = R.string.following_channels_description,
        onboardingType = OnboardingType.FollowingChannels
    ),
    YourLibrary(
        titleId = R.string.your_library,
        descriptionId = R.string.your_library_description,
        onboardingType = OnboardingType.YourLibrary
    ),
}