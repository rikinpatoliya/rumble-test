package com.rumble.domain.channels.channeldetails.domain.domainmodel

enum class UpdateChannelSubscriptionAction(val value: String) {
    SUBSCRIBE("subscribe"),
    UNSUBSCRIBE("unsubscribe"),
    BLOCK("block"),
    UNBLOCK("unblock");
}