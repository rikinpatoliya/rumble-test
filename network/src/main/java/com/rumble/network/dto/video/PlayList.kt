package com.rumble.network.dto.video

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.creator.User

data class PlayList(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("visibility")
    val visibility: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("is_following")
    val isFollowing: Boolean,
    @SerializedName("created_on")
    val createdOn: String,
    @SerializedName("updated_on")
    val updatedOn: String,
    @SerializedName("user")
    val user: User,
    @SerializedName("channel")
    val channel: PlayListChannel?,
    @SerializedName("permissions")
    val permissions: List<String>,
    @SerializedName("num_items")
    val numberOfItems: Int,
    @SerializedName("extra")
    val extra: PlayListExtra?,
    @SerializedName("items")
    val items: List<PlayListItem>,
)

data class PlayListExtra(
    @SerializedName("has_videos_ids")
    val videoIds: List<Int>?,
    @SerializedName("watch_history_paused")
    val watchHistoryPaused: Boolean?,
)