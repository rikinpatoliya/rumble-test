package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

class GiftData(
    @SerializedName("user_id")
    val userId: Long,
    @SerializedName("gift_type")
    val giftType: String,
    @SerializedName("total_gifts")
    val totalGifts: Int?,
    @SerializedName("creator_user_id")
    val creatorUserId: Long,
    @SerializedName("creator_channel_id")
    val creatorChannelId: Long?,
    @SerializedName("users")
    val users: List<GiftDataUser>,
    @SerializedName("channels")
    val channels: List<GiftDataChannel>,
)