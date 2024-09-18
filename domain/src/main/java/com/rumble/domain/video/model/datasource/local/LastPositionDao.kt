package com.rumble.domain.video.model.datasource.local

import androidx.room.*

@Dao
interface LastPositionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLastPosition(lastPosition: RoomLastPosition)

    @Query("SELECT * FROM last_positions WHERE user_id = :userId AND video_id = :videoId")
    suspend fun getLastPosition(userId: String, videoId: Long): RoomLastPosition?

    @Query("DELETE FROM last_positions WHERE user_id = :userId AND video_id = :videoId")
    suspend fun deleteLastPositionForVideo(userId: String, videoId: Long)
    @Transaction
    suspend fun updateLastPosition(lastPosition: RoomLastPosition) {
        deleteLastPositionForVideo(lastPosition.userId, lastPosition.videoId)
        saveLastPosition(lastPosition)
    }
}