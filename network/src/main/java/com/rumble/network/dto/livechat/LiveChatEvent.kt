package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

sealed class LiveChatEvent {

    data class LiveChatInitEvent(
        @SerializedName("type")
        val eventType: LiveChatEventType,
        @SerializedName("data")
        val data: LiveChatInitData
    ) : LiveChatEvent()

    data class LiveChatMessageEvent(
        @SerializedName("request_id")
        val requestId: String,
        @SerializedName("type")
        val eventType: LiveChatEventType,
        @SerializedName("data")
        val messagesData: LiveChatMessageData
    ) : LiveChatEvent()

    data class LiveChatError(
        val message: String
    ) : LiveChatEvent()

    data class LiveChatDeleteMessagesEvent(
        @SerializedName("type")
        val eventType: LiveChatEventType,
        @SerializedName("data")
        val deleteData: LiveChatDeleteMessagesData
    ) : LiveChatEvent()

    data class LiveChatMuteUsersEvent(
        @SerializedName("type")
        val eventType: LiveChatEventType,
        @SerializedName("data")
        val muteUsersData: LiveChatMuteUsersData
    ) : LiveChatEvent()

    data class PinMessageEvent(
        @SerializedName("type")
        val eventType: LiveChatEventType,
        @SerializedName("data")
        val data: LivePinMessageData
    ) : LiveChatEvent()

    data class UnpinMessageEvent(
        @SerializedName("type")
        val eventType: LiveChatEventType,
        @SerializedName("data")
        val data: LivePinMessageData
    ) : LiveChatEvent()

    data class CanModerateEvent(
        @SerializedName("type")
        val eventType: LiveChatEventType,
        @SerializedName("data")
        val data: CanModerateData
    ) : LiveChatEvent()

    data class RaidConfirmedEvent(
        @SerializedName("type")
        val eventType: LiveChatEventType,
        @SerializedName("data")
        val data: Raid
    ) : LiveChatEvent()
    
    data class LiveGateEvent(
        @SerializedName("type")
        val eventType: LiveChatEventType,
        @SerializedName("data")
        val data: LiveGate
    ) : LiveChatEvent()

    data class GiftPurchasedEvent(
        @SerializedName("request_id")
        val requestId: String,
        @SerializedName("type")
        val eventType: LiveChatEventType,
        @SerializedName("data")
        val data: PurchasedGiftData,
    )

    data class GiftReceivedEvent(
        @SerializedName("request_id")
        val requestId: String,
        @SerializedName("type")
        val eventType: LiveChatEventType,
        @SerializedName("data")
        val data: ReceivedGiftData,
    )
}
