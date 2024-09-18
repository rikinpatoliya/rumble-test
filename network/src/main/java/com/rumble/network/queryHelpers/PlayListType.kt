package com.rumble.network.queryHelpers

enum class PlayListType(val id: String) {
    WATCH_HISTORY("watch-history"),
    WATCH_LATER("watch-later"),
    LIKED("liked");

    override fun toString(): String = this.id
}