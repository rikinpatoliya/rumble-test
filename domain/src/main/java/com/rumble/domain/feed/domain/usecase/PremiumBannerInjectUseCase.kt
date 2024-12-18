package com.rumble.domain.feed.domain.usecase

import com.rumble.domain.settings.model.UserPreferenceManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class PremiumBannerInjectUseCase @Inject constructor(
    private val userPreferenceManager: UserPreferenceManager
) {

    operator fun invoke(
        index: Int,
        numberOfColumns: Int,
        isPremium: Boolean
    ): Boolean {
        return index ==  numberOfColumns &&
                isPremium.not() &&
                runBlocking { userPreferenceManager.displayPremiumBannerFlow.first() }
    }
}