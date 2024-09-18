package com.rumble.network.dto.collection

import com.google.gson.annotations.SerializedName

data class CollectionData(

    @SerializedName("collections")
    val collections : List<VideoCollection>
)
