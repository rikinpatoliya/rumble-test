package com.rumble.domain.livechat.domain.usecases

import com.rumble.domain.livechat.domain.domainmodel.EmoteEntity
import com.rumble.utils.RumbleConstants.MAX_VISIBLE_RECENT_EMOTE
import javax.inject.Inject

class SortRecentEmoteListUseCase @Inject constructor() {
    operator fun invoke(emoteList: List<EmoteEntity>) =
        emoteList.sortedWith(
            compareByDescending<EmoteEntity> {
                it.usageCount
            }.thenByDescending {
                it.lastUsageTime
            }
        ).take(MAX_VISIBLE_RECENT_EMOTE)
}