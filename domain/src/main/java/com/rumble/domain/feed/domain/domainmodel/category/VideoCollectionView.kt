package com.rumble.domain.feed.domain.domainmodel.category

import java.util.*

data class VideoCollectionView(
    val id: Long = 0,
    val videoCollectionName: String,
    val viewTimestamp: Date,
    val userId: String,
)
