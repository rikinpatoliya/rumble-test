package com.rumble.domain.livechat.model.repository

import com.rumble.domain.livechat.domain.domainmodel.EmoteEntity
import com.rumble.domain.livechat.model.datasource.local.EmoteDao
import com.rumble.domain.livechat.model.toEmoteEntity
import com.rumble.domain.livechat.model.toRoomEmote
import com.rumble.utils.RumbleConstants.RECENT_MAX_NUMBER
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class RecentEmoteRepositoryImpl(
    private val emoteDao: EmoteDao,
    private val dispatcher: CoroutineDispatcher,
): RecentEmoteRepository {

    override suspend fun saveRecentEmote(emoteEntity: EmoteEntity) = withContext(dispatcher) {
        val totalCount = emoteDao.getTotalCount()
        if (totalCount >= RECENT_MAX_NUMBER) {
            emoteDao.deleteOldest()
        }
        emoteDao.saveOrUpdateItem(emoteEntity.toRoomEmote())
    }

    override suspend fun fetchRecentEmoteList(): List<EmoteEntity> = withContext(dispatcher) {
        emoteDao.fetchAll().map { it.toEmoteEntity() }
    }

    override suspend fun deleteAllRecentEmotes() = withContext(dispatcher) {
        emoteDao.deleteAll()
    }
}