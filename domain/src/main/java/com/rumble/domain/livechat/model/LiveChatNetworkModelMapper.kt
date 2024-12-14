package com.rumble.domain.livechat.model

import com.rumble.domain.livechat.domain.domainmodel.BadgeEntity
import com.rumble.domain.livechat.domain.domainmodel.ChatMode
import com.rumble.domain.livechat.domain.domainmodel.LiveChatChannelEntity
import com.rumble.domain.livechat.domain.domainmodel.LiveChatConfig
import com.rumble.domain.livechat.domain.domainmodel.LiveChatMessageEntity
import com.rumble.domain.livechat.domain.domainmodel.LiveChatResult
import com.rumble.domain.livechat.domain.domainmodel.LiveGateEntity
import com.rumble.domain.livechat.domain.domainmodel.PremiumGiftDetails
import com.rumble.domain.livechat.domain.domainmodel.PremiumGiftEntity
import com.rumble.domain.livechat.domain.domainmodel.PremiumGiftType
import com.rumble.domain.livechat.domain.domainmodel.RaidEntity
import com.rumble.domain.livechat.domain.domainmodel.RaidMessageType
import com.rumble.domain.livechat.domain.domainmodel.RantConfig
import com.rumble.domain.livechat.domain.domainmodel.RantLevel
import com.rumble.network.dto.livechat.LiveChatChannel
import com.rumble.network.dto.livechat.LiveChatConfigRant
import com.rumble.network.dto.livechat.LiveChatEvent
import com.rumble.network.dto.livechat.LiveChatMessage
import com.rumble.network.dto.livechat.LiveChatUser
import com.rumble.network.dto.livechat.PremiumGiftList
import com.rumble.network.dto.livechat.Raid
import com.rumble.utils.extension.convertUtcToLocal
import com.rumble.utils.extension.getComposeColor
import com.rumble.utils.extension.getUserId
import com.rumble.utils.extension.toUserIdString
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
                val userBadges = event.data.users.find { it.id.toUserIdString() == currentUerId }?.badges
                    ?: emptyList()
                val entities = event.data.messages.map { message ->
                    val user = event.data.users.find { it.id == message.userId }
                    val channel = event.data.channels.find { it.id == message.channelId }
                    createEntity(message, user, channel)
                }
                val config =
                    LiveChatConfig(
                        chatId = event.data.chat.id,
                        rantConfig = mapToRantConfig(event.data.config.rants),
                        badges = event.data.config.badges?.mapValues {
                            BadgeEntity(
                                url = baseUrl + it.value.icons.imageUrl,
                                label = it.value.label.english
                            )
                        } ?: emptyMap(),
                        messageMaxLength = event.data.config.messageMaxLength ?: 0,
                        currentUserBadges = userBadges,
                        currencySymbol = CURRENCY,
                        channels = mapToLiveChatChannelEntity(event.data.channels),
                        premiumGiftEntity = event.data.config.giftList?.let { mapToPremiumGiftEntity(it) },
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
                            countDownValue = it.countdown,
                            chatMode = ChatMode.getByValue(it.chatMode),
                        )
                    },
                    raidEntity = mapToRaidEntity(event.data.raid),
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
                        countDownValue = event.data.countdown,
                        chatMode = ChatMode.getByValue(event.data.chatMode),
                    )
                )
            }

            is LiveChatEvent.RaidConfirmedEvent -> {
                LiveChatResult(
                    raidEntity = mapToRaidEntity(event.data)
                )
            }

            else -> LiveChatResult(success = false)
        }

    private fun createEntity(
        message: LiveChatMessage,
        user: LiveChatUser?,
        channel: LiveChatChannel?
    ) = LiveChatMessageEntity(
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
        userNameColor = user?.userNameColor?.getComposeColor(),
        isRaidMessage = message.raidNotification != null,
        raidMessageType = if (message.raidNotification != null) RaidMessageType.getRandomType() else null,
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

    private fun mapToRaidEntity(raid: Raid?) = raid?.let {
        RaidEntity(
            currentChannelName = raid.currentChannelName,
            currentChannelAvatar = raid.currentChannelAvatar,
            targetUrl = raid.targetUrl,
            targetChannelName = raid.targetChannelName,
            targetChannelAvatar = raid.targetChannelAvatar,
            targetVideoTitle = raid.targetVideoTitle,
            timeOut = calculateRaidTimeout(it.redirectionSteps),
        )
    }

    private fun calculateRaidTimeout(redirectionSteps: Map<Float, List<Int>>): Long {
        val defaultDelay = 10
        val defaultSpread = 50
        val random = Math.random() * 100
        val (delay, spread) = redirectionSteps.keys.firstOrNull { random < it }?.let { key ->
            redirectionSteps[key]
        } ?: listOf(defaultDelay, defaultSpread)
        return (delay + Math.random() * spread).toLong()
    }

    private fun mapToPremiumGiftEntity(giftList: PremiumGiftList) =
        PremiumGiftEntity(
            type = PremiumGiftType.getByStringValue(giftList.type),
            giftList = giftList.productList.map {
                PremiumGiftDetails(
                    productId = it.id,
                    priceCents = it.amountCents,
                    giftsAmount = it.totalGifts,
                )
            }
        )
}
