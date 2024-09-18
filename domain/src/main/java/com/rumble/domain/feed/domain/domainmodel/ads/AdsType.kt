package com.rumble.domain.feed.domain.domainmodel.ads

enum class AdsType(val value: String) {
    SPONSORED("sponsored"),
    INTERNAL("internal");

    companion object {
        fun getByValue(value: String): AdsType =
            when (value) {
                "sponsored" -> SPONSORED
                "internal" -> INTERNAL
                else -> throw Error("Unsupported ads type!")
            }
    }
}