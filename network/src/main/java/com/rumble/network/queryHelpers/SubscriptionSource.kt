package com.rumble.network.queryHelpers

enum class SubscriptionSource(val value: String) {
    Home("home"),
    Profile("profile"),
    Video("video"),
    PushNotification("push"),
    ChannelDetails("channel");

    override fun toString(): String = this.value
}