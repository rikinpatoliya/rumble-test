package com.rumble.network.queryHelpers

enum class Date(val value: String) {
    TODAY("today"),
    THIS_WEEK("this-week"),
    THIS_MONTH("this-month"),
    THIS_YEAR("this-year");

    override fun toString(): String = this.value
}
