package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.creator.Channel
import com.rumble.network.dto.creator.User

class ReceivedGiftData(
    @SerializedName("user_id")
    val userId: Long,
    @SerializedName("gift_type")
    val giftType: String,
    @SerializedName("total_gifts")
    val creatorUserId: Long,
    @SerializedName("creator_channel_id")
    val creatorChannelId: Long?,
    @SerializedName("users")
    val users: List<User>,
    @SerializedName("channels")
    val channels: List<Channel>,
) 