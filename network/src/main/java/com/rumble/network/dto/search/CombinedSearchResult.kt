package com.rumble.network.dto.search

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.login.UserState

data class CombinedSearchResult(
    @SerializedName("user")
    val user: UserState,
    @SerializedName("data")
    val data: CombinedSearchData
)
