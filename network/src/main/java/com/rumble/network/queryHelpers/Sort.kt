package com.rumble.network.queryHelpers

enum class Sort(val value: String) {
    VIEWS("views"),
    DATE("date"),
    RUMBLES("rumbles");

    override fun toString(): String = this.value
}
