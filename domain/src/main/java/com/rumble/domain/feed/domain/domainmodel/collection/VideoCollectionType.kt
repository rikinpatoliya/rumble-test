package com.rumble.domain.feed.domain.domainmodel.collection

sealed class VideoCollectionType(val collectionTitle: String? = null) {
    object MyFeed : VideoCollectionType()
    data class VideoCollectionEntity(
        val id: String,
        val slug: String,
        val title: String,
        val thumbnail: String?,
        val type: String,
        val name: String,
        val backsplash: String?,
        val videos: Int,
        val rumbles: Int,
        val followers: Int,
        val following: Int,
        val followed: Boolean,
    ) : VideoCollectionType(title)
}


