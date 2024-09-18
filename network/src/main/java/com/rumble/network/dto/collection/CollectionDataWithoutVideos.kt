package com.rumble.network.dto.collection

import com.google.gson.annotations.SerializedName

data class CollectionDataWithoutVideos(
    @SerializedName("collections")
    val collections: List<VideoCollectionWithoutVideos>,
)
