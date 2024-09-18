package com.rumble.domain.feed.domain.domainmodel.video

enum class VideoType(val extension: String) {
    UNKNOWN(""),
    MP4(".mp4"),
    M3U8(".m3u8")
}