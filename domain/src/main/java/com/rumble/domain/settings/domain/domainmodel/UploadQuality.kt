package com.rumble.domain.settings.domain.domainmodel

import com.rumble.domain.R

enum class UploadQuality(val value: Int, val titleId: Int, val resolution: Int, val bitRate: Int) {
    QUALITY_480(1, R.string.quality_480, 480, 2500000),
    QUALITY_720(2, R.string.quality_720, 720, 5000000),
    QUALITY_1080(3, R.string.quality_1080, 1080, 8000000),
    QUALITY_FULL(4, R.string.quality_full, Int.MAX_VALUE, Int.MAX_VALUE),
    QUALITY_UNDEFINED(5, R.string.quality_undefined, Int.MAX_VALUE, Int.MAX_VALUE);

    companion object {
        val defaultUploadQuality = QUALITY_480

        fun getByValue(value: Int): UploadQuality =
            when (value) {
                1 -> QUALITY_480
                2 -> QUALITY_720
                3 -> QUALITY_1080
                4 -> QUALITY_FULL
                5 -> QUALITY_UNDEFINED
                else -> throw Error("Unsupported UploadQuality type!")
            }
    }
}