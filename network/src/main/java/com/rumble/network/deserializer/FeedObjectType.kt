package com.rumble.network.deserializer

enum class FeedObjectType(val value: String) {
    Video("video"),
    Repost("video_repost");

    companion object {
        fun getByValue(objectValue: String) =
            entries.find { it.value == objectValue }
    }
}