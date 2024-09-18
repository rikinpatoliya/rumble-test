package com.rumble.utils

inline fun <reified T : Enum<T>> valueOfOrNull(type: String?): T? {
    return try {
        java.lang.Enum.valueOf(T::class.java, type)
    } catch (e: IllegalArgumentException) {
        null
    }
}