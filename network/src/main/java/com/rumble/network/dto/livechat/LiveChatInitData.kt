package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

data class LiveChatInitData(
    @SerializedName("chat")
    val chat: LiveChat,
    @SerializedName("messages")
    val messages: List<LiveChatMessage>,
    @SerializedName("users")
    val users: List<LiveChatUser>,
    @SerializedName("config")
    val config: LiveChatConfig,
    @SerializedName("channels")
    val channels: List<LiveChatChannel>,
    @SerializedName("pinned_message")
    val pinnedMessage: LiveChatMessage?,
    @SerializedName("can_moderate")
    val canModerate: Boolean?,
    @SerializedName("raid")
    val raid: Raid?,
    @SerializedName("live_gate")
    val liveGate: LiveGate?,
)
