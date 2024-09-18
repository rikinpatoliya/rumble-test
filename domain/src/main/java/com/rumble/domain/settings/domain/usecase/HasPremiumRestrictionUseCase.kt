package com.rumble.domain.settings.domain.usecase

import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.network.session.SessionManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class HasPremiumRestrictionUseCase @Inject constructor(
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(videoEntity: VideoEntity): Boolean {
        val isPremiumUser = sessionManager.isPremiumUserFlow.first()
        return isPremiumUser.not() && videoEntity.isPremiumExclusiveContent
    }
}