package com.rumble.domain.feed.domain.domainmodel.collection

import com.rumble.domain.common.model.RumbleError

sealed class VideoCollectionResult {
    data class Success(val videoCollections: List<VideoCollectionType>) : VideoCollectionResult()
    data class Failure(val rumbleError: RumbleError) : VideoCollectionResult()
}