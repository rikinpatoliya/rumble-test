package com.rumble.domain.livechat.domain.usecases

import com.rumble.domain.livechat.domain.domainmodel.EmoteEntity
import com.rumble.domain.livechat.model.repository.RecentEmoteRepository
import javax.inject.Inject

class SaveRecentEmoteUseCase @Inject constructor(
    private val recentEmoteRepository: RecentEmoteRepository,
) {
    suspend operator fun invoke(emoteEntity: EmoteEntity) {
        recentEmoteRepository.saveRecentEmote(emoteEntity.copy(
            lastUsageTime = System.currentTimeMillis(),
        ))
    }
}