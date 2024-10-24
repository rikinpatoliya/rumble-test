package com.rumble.domain.livechat.model

import com.rumble.domain.livechat.domain.domainmodel.BadgeEntity
import com.rumble.domain.livechat.domain.domainmodel.LiveChatChannelEntity
import com.rumble.domain.livechat.domain.domainmodel.LiveChatConfig
import com.rumble.domain.livechat.domain.domainmodel.LiveChatMessageEntity
import com.rumble.domain.livechat.domain.domainmodel.LiveChatResult
import com.rumble.domain.livechat.domain.domainmodel.LiveGateEntity
import com.rumble.domain.livechat.domain.domainmodel.RantConfig
import com.rumble.domain.livechat.domain.domainmodel.RantLevel
import com.rumble.network.dto.livechat.LiveChatChannel
import com.rumble.network.dto.livechat.LiveChatConfigRant
import com.rumble.network.dto.livechat.LiveChatEvent
import com.rumble.network.dto.livechat.LiveChatMessage
import com.rumble.network.dto.livechat.LiveChatUser
import com.rumble.utils.extension.convertUtcToLocal
import com.rumble.utils.extension.getComposeColor
import com.rumble.utils.extension.getUserId
import java.math.BigDecimal

object LiveChatNetworkModelMapper {
    private const val CURRENCY =
        "$" //This parameter may come from the server in the future, but for now it's a constant for all messages

    fun mapToLiveChatResult(
        event: LiveChatEvent,
        baseUrl: String,
        currentUerId: String
    ): LiveChatResult =
        when (event) {
            is LiveChatEvent.LiveChatInitEvent -> {
                val userBadges = event.data.users.find { it.id.toString() == currentUerId }?.badges
                    ?: emptyList()
                val entities = event.data.messages.map { message ->
                    val user = event.data.users.find { it.id == message.userId }
                    val channel = event.data.channels.find { it.id == message.channelId }
                    createEntity(message, user, channel)
                }
                val config =
                    LiveChatConfig(
                        event.data.chat.id,
                        mapToRantConfig(event.data.config.rants),
                        badges = event.data.config.badges?.mapValues {
                            BadgeEntity(
                                url = baseUrl + it.value.icons.imageUrl,
                                label = it.value.label.english
                            )
                        } ?: emptyMap(),
                        messageMaxLength = event.data.config.messageMaxLength ?: 0,
                        currentUserBadges = userBadges,
                        currencySymbol = CURRENCY,
                        channels = mapToLiveChatChannelEntity(event.data.channels)
                    )
                LiveChatResult(
                    messageList = entities,
                    initialConfig = true,
                    liveChatConfig = config,
                    pinnedMessageId = event.data.pinnedMessage?.id,
                    canModerate = event.data.canModerate ?: false,
                    liveGate = event.data.liveGate?.let {
                        LiveGateEntity(
                            videoTimeCode = it.timeCode,
                            countDownValue = it.countdown
                        )
                    },
                )
            }

            is LiveChatEvent.LiveChatMessageEvent -> {
                val entities = event.messagesData.messages.map { message ->
                    val user = event.messagesData.users.find { it.id == message.userId }
                    val channel = event.messagesData.channels.find { it.id == message.channelId }
                    createEntity(message, user, channel)
                }
                LiveChatResult(messageList = entities)
            }

            is LiveChatEvent.LiveChatDeleteMessagesEvent -> {
                LiveChatResult(deletedMessageIdList = event.deleteData.messageIdList)
            }

            is LiveChatEvent.LiveChatMuteUsersEvent -> {
                LiveChatResult(mutedUserIdList = event.muteUsersData.userIdList)
            }

            is LiveChatEvent.PinMessageEvent -> {
                LiveChatResult(pinnedMessageId = event.data.message.id)
            }

            is LiveChatEvent.UnpinMessageEvent -> {
                LiveChatResult(unpinnedMessageId = event.data.message.id)
            }

            is LiveChatEvent.CanModerateEvent -> {
                if (event.data.userIdList.contains(currentUerId.getUserId())) {
                    LiveChatResult(canModerate = event.data.canModerate)
                } else {
                    LiveChatResult(canModerate = null)
                }
            }

            is LiveChatEvent.LiveGateEvent -> {
                LiveChatResult(
                    liveGate = LiveGateEntity(
                        videoTimeCode = event.data.timeCode,
                        countDownValue = event.data.countdown
                    )
                )
            }

            else -> LiveChatResult(success = false)
        }

    private fun createEntity(message: LiveChatMessage, user: LiveChatUser?, channel: LiveChatChannel?) = LiveChatMessageEntity(
        messageId = message.id,
        userId = user?.id ?: 0,
        channelId = message.channelId,
        userName = channel?.username ?: (user?.userName ?: ""),
        userThumbnail = if (channel != null) channel.image else user?.image,
        message = message.text,
        timeReceived = message.time.convertUtcToLocal(),
        rantPrice = message.rant?.priceCents?.let {
            BigDecimal(it).movePointLeft(2)
        },
        badges = user?.badges ?: emptyList(),
        currencySymbol = CURRENCY,
        isNotification = message.notification != null,
        notification = message.notification?.text,
        notificationBadge = message.notification?.badge,
        userNameColor = user?.userNameColor?.getComposeColor()
    )

    private fun mapToRantConfig(rants: LiveChatConfigRant): RantConfig =
        RantConfig(
            levelList = rants.levels.map {
                RantLevel(
                    rantPrice = BigDecimal(it.priceDollar),
                    duration = it.duration,
                    backgroundColor = it.colors.bg2,
                    foregroundColor = it.colors.fg,
                    mainColor = it.colors.main,
                    rantId = it.idList.firstOrNull() ?: ""
                )
            },
            rantsEnabled = rants.enable ?: false
        )

    private fun mapToLiveChatChannelEntity(channels: List<LiveChatChannel>): List<LiveChatChannelEntity> =
        channels.map {
            LiveChatChannelEntity(
                id = it.id,
                username = it.username,
                image = it.image ?: ""
            )
        }
}
