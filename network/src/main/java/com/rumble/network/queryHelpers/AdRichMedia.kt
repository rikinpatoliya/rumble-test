package com.rumble.network.queryHelpers

enum class AdRichMedia(val value: Int) {
    INCLUDE(1),
    NOT_INCLUDE(0);

    override fun toString(): String = this.value.toString()
}