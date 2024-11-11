package com.rumble.domain.livechat.model.datasource.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emotes")
data class RoomEmote(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "url")
    val url: String,
    @ColumnInfo(name = "usage_count")
    val usageCount: Int = 0,
    @ColumnInfo(name = "last_usage_time")
    val lastUsageTime: Long = 0,
)
