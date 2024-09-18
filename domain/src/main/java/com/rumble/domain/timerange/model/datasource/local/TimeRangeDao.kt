package com.rumble.domain.timerange.model.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TimeRangeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTimeRange(timeRange: RoomTimeRange)

    @Query("SELECT * FROM time_ranges")
    suspend fun getAllTimeRanges(): List<RoomTimeRange>

    @Query("DELETE FROM time_ranges")
    suspend fun bulkDelete()
}