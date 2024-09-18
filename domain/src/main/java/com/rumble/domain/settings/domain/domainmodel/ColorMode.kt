package com.rumble.domain.settings.domain.domainmodel

enum class ColorMode(val value: Int) {
    SYSTEM_DEFAULT(1),
    LIGHT_MODE(2),
    DARK_MODE(3);

    companion object {
        fun getByValue(value: Int): ColorMode =
            when (value) {
                1 -> SYSTEM_DEFAULT
                2 -> LIGHT_MODE
                3 -> DARK_MODE
                else -> throw Error("Unsupported ColorMode type!")
            }
    }
}

fun ColorMode.isDarkTheme(systemDefaultDarkMode: Boolean): Boolean {
   return when (this) {
        ColorMode.SYSTEM_DEFAULT -> systemDefaultDarkMode
        ColorMode.LIGHT_MODE -> false
        ColorMode.DARK_MODE -> true
    }
}