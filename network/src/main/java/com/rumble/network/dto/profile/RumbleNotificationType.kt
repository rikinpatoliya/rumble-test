package com.rumble.network.dto.profile

enum class RumbleNotificationType(val value: String) {
    EARN("earn"),
    VIDEO_LIVE("video_live"),
    VIDEO_BATTLE("video_battle"),
    FOLLOW("follow"),
    TAG("tag"),
    COMMENT("comment"),
    COMMENT_REPLY("comment_reply"),
    NEW_VIDEO("new_video");

    override fun toString(): String = this.value
}