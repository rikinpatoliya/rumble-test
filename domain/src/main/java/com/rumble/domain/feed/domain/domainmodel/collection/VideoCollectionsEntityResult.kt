package com.rumble.domain.feed.domain.domainmodel.collection

import com.rumble.domain.common.model.RumbleError

sealed class VideoCollectionsEntityResult {
    data class Success(val videoCollections: List<VideoCollectionType.VideoCollectionEntity>) :
        VideoCollectionsEntityResult()

    data class Failure(val rumbleError: RumbleError) : VideoCollectionsEntityResult()
}