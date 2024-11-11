package com.rumble.domain.livechat.domain.usecases

import com.rumble.domain.livechat.domain.domainmodel.EmoteEntity
import javax.inject.Inject

class AddRecentEmoteListUseCase @Inject constructor(
    private val sortRecentEmoteListUseCase: SortRecentEmoteListUseCase,
) {
    operator fun invoke(
        emoteList: List<EmoteEntity>,
        lastUsedEmote: EmoteEntity
    ): List<EmoteEntity> {
        val temp = if (emoteList.find { it.url == lastUsedEmote.url } != null) emoteList.map {
            if (it.url == lastUsedEmote.url) {
                it.copy(
                    usageCount = it.usageCount + 1,
                    lastUsageTime = System.currentTimeMillis(),
                )
            } else {
                it
            }
        } else emoteList + listOf(
            lastUsedEmote.copy(
                usageCount = 1,
                lastUsageTime = System.currentTimeMillis(),
            )
        )

        return sortRecentEmoteListUseCase(temp)
    }
}