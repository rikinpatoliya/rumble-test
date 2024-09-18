package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.login.UserState

data class EmoteListResponse(
    @SerializedName("user")
    val user: UserState,
    @SerializedName("data")
    val data: EmoteListData
)

data class EmoteListData(
    @SerializedName("items")
    val items: List<EmoteItem>
)

data class EmoteItem(
    @SerializedName("id")
    val id: Int,
    @SerializedName("emotes")
    val emotes: List<Emote>
)

data class Emote(
    @SerializedName("name")
    val name: String,
    @SerializedName("is_subs_only")
    val subscribersOnly: Boolean,
    @SerializedName("file")
    val file: String
)
