package com.rumble.network.dto.livechat

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonNull
import com.google.gson.annotations.SerializedName

data class LiveChatMessageData(
    @SerializedName("messages")
    val messages: List<LiveChatMessage>,
    @SerializedName("users")
    val users: List<LiveChatUser>,
    @SerializedName("channels")
    private val _channels: JsonArray
) {
    val channels: List<LiveChatChannel>
        get() {
            val gson = Gson()
            return try {
                val first = _channels.firstOrNull()
                if (first is JsonArray || first is JsonNull) {
                    emptyList()
                } else {
                   _channels.map {
                       gson.fromJson(it, LiveChatChannel::class.java)
                   }
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
}

