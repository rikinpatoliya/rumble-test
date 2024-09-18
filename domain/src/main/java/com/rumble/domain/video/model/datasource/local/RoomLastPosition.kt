package com.rumble.domain.video.model.datasource.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "last_positions")
data class RoomLastPosition(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,
    @ColumnInfo(name = "video_id")
    val videoId: Long,
    @ColumnInfo(name = "last_position")
    val lastPosition: Long,
    @ColumnInfo(name = "user_id")
    val userId: String
)
