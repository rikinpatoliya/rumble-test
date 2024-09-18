package com.rumble.network.queryHelpers

enum class CategoryVideoType(val value: String) {
    LIVE("live"),
    STREAMED("streamed"),
    REGULAR("regular");

    override fun toString(): String = this.value
}