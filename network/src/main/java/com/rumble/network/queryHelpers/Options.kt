package com.rumble.network.queryHelpers

enum class Options(val value: String) {
    WATCHING_PROGRESS("video.watching_progress"),
    FULL("video.full"),
    EXTENDED("extended"),
    RELATED("related"),
    COMMENTS("comments"),
    CATEGORIES("categories"),
    USER_REPOSTS("video.user_reposts");

    override fun toString(): String = this.value
}