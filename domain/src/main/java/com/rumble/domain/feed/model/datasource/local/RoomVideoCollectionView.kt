package com.rumble.domain.feed.model.datasource.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.rumble.domain.onboarding.domain.domainmodel.Converters
import java.util.*

@Entity(tableName = "VideoCollectionView")
@TypeConverters(Converters::class)
data class RoomVideoCollectionView(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "viewTimestamp")
    val viewTimestamp: Date,
    @ColumnInfo(name = "userId")
    val userId: String,
)
