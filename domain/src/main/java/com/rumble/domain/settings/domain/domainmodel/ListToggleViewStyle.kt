package com.rumble.domain.settings.domain.domainmodel

enum class ListToggleViewStyle(val value: Int) {
    GRID(1),
    LIST(2);

    companion object {
        fun getByValue(value: Int): ListToggleViewStyle =
            when (value) {
                1 -> GRID
                2 -> LIST
                else -> throw Error("Unsupported ListToggleViewStyle type!")
            }
    }
}