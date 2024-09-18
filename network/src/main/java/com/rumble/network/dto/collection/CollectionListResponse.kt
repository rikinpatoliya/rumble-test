package com.rumble.network.dto.collection

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.login.UserState

data class CollectionListResponse(
    @SerializedName("user")
    val user: UserState,
    @SerializedName("data")
    val data: CollectionData

)