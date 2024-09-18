package com.rumble.network.dto

enum class LiveStreamStatus(val value: Int) {
    UNKNOWN(-1),
    ENDED(0),
    OFFLINE(1),
    LIVE(2);

    companion object {
        fun get(value: Int?): LiveStreamStatus =
            when (value) {
                -1 -> UNKNOWN
                0 -> ENDED
                1 -> OFFLINE
                2 -> LIVE
                else -> UNKNOWN
            }
    }
}