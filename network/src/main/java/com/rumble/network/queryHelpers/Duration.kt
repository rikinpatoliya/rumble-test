package com.rumble.network.queryHelpers

enum class Duration(val value: String) {
    SHORT("short"),
    LONG("long");

    override fun toString(): String = this.value
}