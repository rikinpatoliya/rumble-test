package com.rumble.domain.profile.domainmodel

enum class Gender(val value: String, val genderId: Int, val requestValue: String) {
    Mail("male", 1, "male"),
    Female("female", 2, "female"),
    Unspecified("unspecified", 3, "");

    companion object {
        fun getByValue(value: String?): Gender =
            values().find { it.value == value } ?: Unspecified
    }
}