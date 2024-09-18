package com.rumble.domain.settings.domain.domainmodel

data class NotificationSettingsEntity(
    val legacyValues: NotificationSettingsLegacyEntity,
    val moneyEarned: Boolean,
    val videoApprovedForMonetization: Boolean,
    val someoneFollowsYou: Boolean,
    val someoneTagsYou: Boolean,
    val commentsOnYourVideo: Boolean,
    val repliesToYourComments: Boolean,
    val newVideoBySomeoneYouFollow: Boolean
)

data class NotificationSettingsLegacyEntity(
    val mediaApproved: Boolean,
    val mediaComment: Boolean,
    val commentReplied: Boolean,
    val battlePosted: Boolean,
    val winMoney: Boolean,
    val videoTrending: Boolean,
    val allowPush: Boolean,
)