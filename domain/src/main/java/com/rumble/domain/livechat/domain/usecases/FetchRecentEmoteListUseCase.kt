package com.rumble.domain.livechat.domain.usecases

import com.rumble.domain.livechat.domain.domainmodel.EmoteEntity
import com.rumble.domain.livechat.domain.domainmodel.EmoteGroupEntity
import com.rumble.domain.livechat.model.repository.RecentEmoteRepository
import javax.inject.Inject

class FetchRecentEmoteListUseCase @Inject constructor(
    private val recentEmoteRepository: RecentEmoteRepository,
    private val sortRecentEmoteListUseCase: SortRecentEmoteListUseCase,
) {
    suspend operator fun invoke(remoteEmoteGroups: List<EmoteGroupEntity>): List<EmoteEntity> {
        val recentEmoteList = recentEmoteRepository.fetchRecentEmoteList().filter { recentEmote ->
            remoteEmoteGroups.any { emoteGroup -> emoteGroup.emoteList.find { it.url == recentEmote.url } != null }
        }
        return sortRecentEmoteListUseCase(recentEmoteList)
    }
}