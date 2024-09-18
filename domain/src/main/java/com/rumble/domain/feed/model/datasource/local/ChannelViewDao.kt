package com.rumble.domain.feed.model.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChannelViewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(roomChannelView: RoomChannelView)

    @Query("SELECT * FROM channelView where channelId = :channelId")
    suspend fun getByChannelId(channelId: String): RoomChannelView?
}