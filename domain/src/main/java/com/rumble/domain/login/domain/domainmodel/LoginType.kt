package com.rumble.domain.login.domain.domainmodel

import com.rumble.domain.R

enum class LoginType(val value: Int, val stringId: Int, val provider: String) {

    UNKNOWN(0, R.string.unknown, ""),
    FACEBOOK(1, R.string.facebook, "facebook"),
    GOOGLE(2, R.string.google, "user.register.google"),
    APPLE(3, R.string.apple, "user.register.apple"),
    RUMBLE(4, R.string.rumble, "newuser");

    companion object {
        fun getByValue(value: Int): LoginType =
            when (value) {
                0 -> UNKNOWN
                1 -> FACEBOOK
                2 -> GOOGLE
                3 -> APPLE
                4 -> RUMBLE
                else -> throw Error("Unsupported LoginType!")
            }
    }
}