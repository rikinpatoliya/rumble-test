package com.rumble.domain.timerange.model.datasource.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watch_progress")
data class RoomWatchProgress(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,
    @ColumnInfo(name = "video_id")
    val videoId: Long,
    @ColumnInfo(name = "start_time")
    val startTime: Float?,
    @ColumnInfo(name = "duration")
    val duration: Float,
    @ColumnInfo(name = "is_placeholder")
    val isPlaceholder: Boolean?,
    @ColumnInfo(name = "playback_rate")
    val playbackRate: Float?,
    @ColumnInfo(name = "device_volume")
    val deviceVolume: Int?,
    @ColumnInfo(name = "muted")
    val muted: Boolean?,
    @ColumnInfo(name = "ui_type")
    val uiType: String?
)

