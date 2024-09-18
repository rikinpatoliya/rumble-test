package com.rumble.network.dto.video

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.login.UserState

data class PlayListsResponse(
    @SerializedName("user")
    val user: UserState,
    @SerializedName("data")
    val playListsData: PlayListsData
)

data class PlayListsData(
    @SerializedName("items")
    val playLists: List<PlayList>,
)