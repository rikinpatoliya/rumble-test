package com.rumble.network.queryHelpers

enum class Frequency(val value: Int) {
    DAILY(1),
    INSTANT(2),
    WEEKLY(3),
    MONTHLY(4);

    override fun toString(): String = this.value.toString()
}