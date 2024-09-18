package com.rumble.domain.videolist.domain.model

import com.rumble.domain.library.domain.model.LibraryCollection
import com.rumble.network.queryHelpers.VideoCollectionId

sealed class VideoListType {
    class Collection(val id: VideoCollectionId) : VideoListType()
    class PlayList(val libraryCollection: LibraryCollection) : VideoListType()
    object Live : VideoListType()
    object Popular : VideoListType()
    object Battles : VideoListType()
}

/**
 * VideoListScreen supported video list types
 */
enum class VideoList(val type: VideoListType) {
    Viral(VideoListType.Collection(VideoCollectionId.Viral)),
    Cooking(VideoListType.Collection(VideoCollectionId.Cooking)),
    Sports(VideoListType.Collection(VideoCollectionId.Sports)),
    Gaming(VideoListType.Collection(VideoCollectionId.Gaming)),
    News(VideoListType.Collection(VideoCollectionId.News)),
    Science(VideoListType.Collection(VideoCollectionId.Science)),
    Technology(VideoListType.Collection(VideoCollectionId.Technology)),
    Auto(VideoListType.Collection(VideoCollectionId.Auto)),
    HowTo(VideoListType.Collection(VideoCollectionId.HowTo)),
    Travel(VideoListType.Collection(VideoCollectionId.Travel)),
    Music(VideoListType.Collection(VideoCollectionId.Music)),
    Vlogs(VideoListType.Collection(VideoCollectionId.Vlogs)),
    Podcasts(VideoListType.Collection(VideoCollectionId.Podcasts)),
    Entertainment(VideoListType.Collection(VideoCollectionId.Entertainment)),
    Finance(VideoListType.Collection(VideoCollectionId.Finance)),
    EditorPicks(VideoListType.Collection(VideoCollectionId.EditorPicks)),
    Live(VideoListType.Live),
    Popular(VideoListType.Popular),
    Battles(VideoListType.Battles),
    LibraryWatchLater(VideoListType.PlayList(LibraryCollection.WatchLater)),
    LibraryWatchHistory(VideoListType.PlayList(LibraryCollection.WatchHistory)),
    LibraryPurchases(VideoListType.PlayList(LibraryCollection.Purchases)),
    LibraryLiked(VideoListType.PlayList(LibraryCollection.Liked)),
}