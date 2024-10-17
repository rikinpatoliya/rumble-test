package com.rumble.domain.settings.domain.domainmodel

enum class DebugAdType(val value: Int) {
    REAL_AD(1),
    DEBUG_AD(2),
    CUSTOM_AD_TAG(3);

    companion object {
        fun getByValue(value: Int): DebugAdType =
            when (value) {
                1 -> REAL_AD
                2 -> DEBUG_AD
                3 -> CUSTOM_AD_TAG
                else -> throw Error("Unsupported DebugAdType !")
            }
    }
}