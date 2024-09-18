package com.rumble.battles.library.presentation.library

import com.rumble.battles.R

enum class LibraryScreenSection(
    val sectionTitleId: Int,
    val sectionIconId: Int,
    val sectionEmptyTextId: Int?,
) {
    WatchHistory(R.string.watch_history, R.drawable.ic_clock_history, R.string.no_videos_watch_history),
    Purchases(R.string.purchases, R.drawable.ic_dollar_24, null),
    WatchLater(R.string.watch_later, R.drawable.ic_time, R.string.no_videos_watch_later),
    Playlists(R.string.playlists, R.drawable.ic_playlist, R.string.no_playlists_saved),
    LikedVideos(R.string.liked_videos, R.drawable.ic_like_dislike, R.string.no_videos_liked_videos);
}