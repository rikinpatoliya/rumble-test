package com.rumble.network.queryHelpers

enum class Type(val value: String) {
    CHANNEL("channel"),
    USER("user"),
    MEDIA("media");

    override fun toString(): String = this.value

    companion object {
        fun getByValue(value: String): Type =
            when (value) {
                "channel" -> CHANNEL
                "user" -> USER
                "media" -> MEDIA
                else -> throw Error("Unsupported type!")
            }
    }
}