package com.rumble.domain.settings.domain.domainmodel

enum class BackgroundPlay(val value: Int) {
    PICTURE_IN_PICTURE(1),
    SOUND(2),
    OFF(3);

    companion object {
        fun getByValue(value: Int): BackgroundPlay =
            when (value) {
                1 -> PICTURE_IN_PICTURE
                2 -> SOUND
                3 -> OFF
                else -> throw Error("Unsupported BackgroundPlay type!")
            }
    }
}