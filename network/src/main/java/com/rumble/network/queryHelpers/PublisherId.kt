package com.rumble.network.queryHelpers

enum class PublisherId(val value: String) {
    AndroidApp("6"),
    AndroidTv("8"),
    FireTv("9");

    override fun toString(): String = this.value
}