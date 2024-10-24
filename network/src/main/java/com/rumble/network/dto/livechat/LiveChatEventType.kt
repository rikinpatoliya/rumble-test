package com.rumble.network.dto.livechat

enum class LiveChatEventType(val value: String) {
    UNKNOWN(""),
    INIT("init"),
    MESSAGE("messages"),
    DELETE_MESSAGES("delete_messages"),
    DELETE_NOT_RANT_MESSAGES("delete_non_rant_messages"),
    MUTE_USERS("mute_users"),
    PIN_MESSAGE("pin_message"),
    UNPIN_MESSAGE("unpin_message"),
    CAN_MODERATE("can_moderate"),
    LIVE_GATE("live_gate");

    override fun toString(): String = this.value

    companion object {
        fun getByValue(value: String?): LiveChatEventType =
            LiveChatEventType.values().find { it.value == value } ?: UNKNOWN
    }
}