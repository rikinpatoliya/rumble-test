package com.rumble.domain.livechat.model.datasource.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface EmoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveEmote(emote: RoomEmote)

    @Delete
    suspend fun deleteEmote(emote: RoomEmote)

    @Query("SELECT * FROM emotes")
    suspend fun fetchAll(): List<RoomEmote>

    @Query("DELETE FROM emotes")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM emotes")
    suspend fun getTotalCount(): Int

    @Query("DELETE FROM emotes WHERE last_usage_time = (SELECT MIN(last_usage_time) FROM emotes)")
    suspend fun deleteOldest()

    @Query("SELECT * FROM emotes WHERE url = :url LIMIT 1")
    suspend fun getItemByUrl(url: String): RoomEmote?

    @Query("UPDATE emotes SET usage_count = usage_count + 1 WHERE id = :id")
    suspend fun incrementItemCount(id: Long)

    @Transaction
    suspend fun saveOrUpdateItem(newItem: RoomEmote) {
        getItemByUrl(newItem.url)?.id?.let {
            incrementItemCount(it)
        } ?: run {
            saveEmote(newItem.copy(usageCount = 1))
        }
    }
}