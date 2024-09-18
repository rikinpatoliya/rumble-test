package com.rumble.domain.feed.model.datasource.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "channelView", indices = [Index(value = ["channelId"], unique = true)])
data class RoomChannelView(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,
    @ColumnInfo(name = "channelId")
    val channelId: String,
    @ColumnInfo(name = "time")
    val time: Long
)