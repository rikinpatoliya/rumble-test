package com.rumble.domain.search.model.datasource.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "queries")
data class RoomQuery(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,
    @ColumnInfo(name = "query")
    val query: String,
    @ColumnInfo(name = "time")
    val time: Long
)
