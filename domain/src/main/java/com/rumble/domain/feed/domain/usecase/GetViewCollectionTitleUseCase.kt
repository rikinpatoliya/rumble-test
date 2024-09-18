package com.rumble.domain.feed.domain.usecase

import com.rumble.domain.feed.domain.domainmodel.collection.VideoCollectionType
import javax.inject.Inject

class GetViewCollectionTitleUseCase @Inject constructor() {
    operator fun invoke(viewCollectionType: VideoCollectionType, defaultTitle: String): String {
        viewCollectionType.collectionTitle?.let {
            return it
        } ?: run {
            return defaultTitle
        }
    }
}