package com.rumble.network.queryHelpers

enum class Options(val value: String) {
    WATCHING_PROGRESS("video.watching_progress"),
    FULL("video.full"),
    EXTENDED("extended"),
    RELATED("related"),
    COMMENTS("comments"),
    CATEGORIES("categories"),
    USER_REPOST("video.user_repost");

    override fun toString(): String = this.value
}