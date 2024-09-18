package com.rumble.domain.channels.channeldetails.domain.domainmodel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "channelFollow", indices = [Index(value = ["channelId"], unique = true)])
data class RoomChannelFollow(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,
    @ColumnInfo(name = "channelId")
    val channelId: String,
    @ColumnInfo(name = "followStatus")
    val followed: Boolean,
    @ColumnInfo(name = "time")
    val time: Long,
)