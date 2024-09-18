package com.rumble.domain.video.model

sealed class VideoOption() {
    data class RemoveFromPlayList(val playListId: String) : VideoOption()
    object RemoveFromWatchHistory : VideoOption()
    object RemoveFromWatchLater : VideoOption()
    object SaveToWatchLater : VideoOption()
    object SaveToPlayList : VideoOption()
    object Share : VideoOption()
}