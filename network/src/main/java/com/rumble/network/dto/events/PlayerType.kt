package com.rumble.network.dto.events

enum class PlayerType(val value: String) {
    FEED("f"),
    VIDEO_PAGE("v"),
    DISCOVER_PLAYER("d"),
    TV_PLAYER("tv");
    override fun toString(): String = this.value
}