package com.rumble.network.dto.search

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.creator.Creator
import com.rumble.network.dto.discover.Category
import com.rumble.network.dto.login.UserState

data class AutoCompleteResult(
    @SerializedName("user")
    val user: UserState,
    @SerializedName("data")
    val data: AutoCompleteData,
)

data class AutoCompleteData(
    @SerializedName("channels")
    val channels: List<Creator>,
    @SerializedName("categories")
    val categories: List<Category>,
)