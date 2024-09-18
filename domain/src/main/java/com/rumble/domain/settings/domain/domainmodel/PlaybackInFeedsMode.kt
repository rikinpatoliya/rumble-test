package com.rumble.domain.settings.domain.domainmodel

enum class PlaybackInFeedsMode(val value: Int) {
    ALWAYS_ON(1),
    WIFI_ONLY(2),
    OFF(3);

    companion object {
        fun getByValue(value: Int): PlaybackInFeedsMode =
            when (value) {
                1 -> ALWAYS_ON
                2 -> WIFI_ONLY
                3 -> OFF
                else -> throw Error("Unsupported PlaybackInFeedsMode type!")
            }
    }
}