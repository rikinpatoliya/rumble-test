package com.rumble.domain.channels.channeldetails.domain.domainmodel

enum class ChannelType(val value: String) {
    CHANNEL("channel"),
    USER("user"),
    MEDIA("media");

    override fun toString(): String = this.value

    companion object {
        fun getByValue(value: String): ChannelType =
            when (value) {
                "channel" -> CHANNEL
                "user" -> USER
                "media" -> MEDIA
                else -> throw Error("Unsupported channel type!")
            }
    }
}