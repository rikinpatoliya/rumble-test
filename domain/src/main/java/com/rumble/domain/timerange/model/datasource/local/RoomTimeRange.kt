package com.rumble.domain.timerange.model.datasource.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "time_ranges")
data class RoomTimeRange(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,
    @ColumnInfo(name = "video_id")
    val videoId: Long,
    @ColumnInfo(name = "start_time")
    val startTime: Float?,
    @ColumnInfo(name = "duration")
    val duration: Float
)