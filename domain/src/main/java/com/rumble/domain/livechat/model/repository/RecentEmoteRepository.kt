package com.rumble.domain.livechat.model.repository

import com.rumble.domain.livechat.domain.domainmodel.EmoteEntity

interface RecentEmoteRepository {

    suspend fun saveRecentEmote(emoteEntity: EmoteEntity)

    suspend fun fetchRecentEmoteList(): List<EmoteEntity>

    suspend fun deleteAllRecentEmotes()
}