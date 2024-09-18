package com.rumble.domain.library.domain.model

sealed class PlayListOption() {
    object DeleteWatchHistory : PlayListOption()
    object PlayListSettings : PlayListOption()
    object DeletePlayList : PlayListOption()
}