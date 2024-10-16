package com.rumble.network.queryHelpers

enum class SubscriptionSource(val value: String) {
    Home("home"),
    Profile("profile"),
    Video("video"),
    PushNotification("push");

    override fun toString(): String = this.value
}