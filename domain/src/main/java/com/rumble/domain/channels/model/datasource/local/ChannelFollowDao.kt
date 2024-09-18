package com.rumble.domain.channels.model.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rumble.domain.channels.channeldetails.domain.domainmodel.RoomChannelFollow
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelFollowDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(roomChannelFollow: RoomChannelFollow)

    @Query("SELECT * FROM channelFollow WHERE time > :currentTime")
    fun getChannelFollowUpdatesAfterCurrentTime(currentTime: Long): Flow<List<RoomChannelFollow>>
}