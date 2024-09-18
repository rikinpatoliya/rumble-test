package com.rumble.domain.camera.model.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveVideo(video: RoomVideo)

    @Update
    suspend fun updateVideo(video: RoomVideo)

    @Query("SELECT * FROM videos")
    fun fetchVideoList(): Flow<List<RoomVideo>>

    @Query("SELECT * FROM videos WHERE `status` LIKE :status")
    fun fetchVideoListByStatus(status: Int): List<RoomVideo>

    @Query("SELECT * FROM videos WHERE `uuid` LIKE :uuid")
    suspend fun fetchByUuid(uuid: String): RoomVideo?

    @Query("DELETE FROM videos WHERE `uuid` LIKE :uuid")
    suspend fun deleteVideo(uuid: String)
}