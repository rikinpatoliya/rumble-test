package com.rumble.domain.feed.domain.usecase

import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.utils.RumbleConstants
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class PremiumBannerInjectUseCase @Inject constructor(
    private val userPreferenceManager: UserPreferenceManager
) {

    operator fun invoke(
        before: Feed?,
        after: Feed?,
        numberOfColumns: Int,
        isPremium: Boolean
    ): Boolean {
        return shouldIndexInsert(before, after, numberOfColumns) &&
                isPremium.not() &&
                runBlocking { userPreferenceManager.displayPremiumBannerFlow.first() }
    }

    private fun shouldIndexInsert(
        before: Feed?,
        after: Feed?,
        numberOfColumns: Int
    ): Boolean {
        return if (numberOfColumns == RumbleConstants.HOME_SCREEN_ROWS_3)
            after?.index == numberOfColumns - 1
        else
            before?.index == 0 && after?.index == numberOfColumns - 1
    }
}