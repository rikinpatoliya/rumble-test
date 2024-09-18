package com.rumble.domain.library.domain.model

import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import com.rumble.network.queryHelpers.PlayListType

sealed class LibraryCollectionType {
    data class Library(val playListType: PlayListType) : LibraryCollectionType()
    data class PlayList(val playListEntity: PlayListEntity) : LibraryCollectionType()
    object Purchases : LibraryCollectionType()
}

enum class LibraryCollection(val type: LibraryCollectionType) {
    WatchHistory(LibraryCollectionType.Library(PlayListType.WATCH_HISTORY)),
    WatchLater(LibraryCollectionType.Library(PlayListType.WATCH_LATER)),
    Liked(LibraryCollectionType.Library(PlayListType.LIKED)),
    Purchases(LibraryCollectionType.Purchases)
}