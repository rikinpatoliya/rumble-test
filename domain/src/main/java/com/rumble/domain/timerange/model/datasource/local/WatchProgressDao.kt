package com.rumble.domain.timerange.model.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WatchProgressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWatchProgress(watchProgress: RoomWatchProgress)

    @Query("SELECT * FROM watch_progress")
    suspend fun getAllWatchProgress(): List<RoomWatchProgress>

    @Query("DELETE FROM watch_progress")
    suspend fun bulkDelete()
}