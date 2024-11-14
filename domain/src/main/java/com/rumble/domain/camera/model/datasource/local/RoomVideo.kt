package com.rumble.domain.camera.model.datasource.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class RoomVideo(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "uuid")
    val uuid: String,
    @ColumnInfo(name = "video_url")
    val videoUrl: String,
    @ColumnInfo(name = "extension")
    val videoExtension: String,
    @ColumnInfo(name = "title")
    val title: String = "",
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "tags")
    val tags: String = "",
    @ColumnInfo(name = "licence")
    val licence: Int,
    @ColumnInfo(name = "rights", defaultValue = "false")
    val rights: Boolean,
    @ColumnInfo(name = "terms", defaultValue = "false")
    val terms: Boolean,
    @ColumnInfo(name = "channel_id")
    val channelId: Long,
    @ColumnInfo(name = "info_who")
    val infoWho: String?,
    @ColumnInfo(name = "info_where")
    val infoWhere: String?,
    @ColumnInfo(name = "info_ext_user")
    val infoExtUser: String?,
    @ColumnInfo(name = "visibility")
    val visibility: String,
    @ColumnInfo(name = "status")
    val status: Int,
    @ColumnInfo(name = "status_reported", defaultValue = "false")
    val userNotifiedAboutStatus: Boolean = false,
    @ColumnInfo(name = "publish_date")
    val publishDate: Long?,
    @ColumnInfo(name = "error_message")
    val errorMessage: String?,
    @ColumnInfo(name = "progress", defaultValue = "0")
    val progress: Float = 0F,
    @ColumnInfo(name = "trim_start")
    val trimStart: Float?,
    @ColumnInfo(name = "trim_end")
    val trimEnd: Float?,
    @ColumnInfo(name = "upload_quality", defaultValue = "1")
    val uploadQuality: Int = 1,
    @ColumnInfo(name = "temp_thumb_url")
    val tempThumbUrl: String?,
    @ColumnInfo(name = "temp_video_url")
    val tempVideoUrl: String?,
    @ColumnInfo(name = "uploaded_thumb_ref")
    val uploadedThumbRef: String?,
    @ColumnInfo(name = "uploaded_video_ref")
    val uploadedVideoRef: String?,
    @ColumnInfo(name = "site_channel_id")
    val siteChannelId: Int?,
    @ColumnInfo(name = "media_channel_id")
    val mediaChannelId: Int?
)
