package com.rumble.battles.commonViews

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.battles.common.getNotificationIcon
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.enforcedWhite
import com.rumble.theme.imageXXSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusXLarge
import com.rumble.theme.rumbleGreen

@Composable
fun NotificationActionButton(
    notificationActionData: NotificationActionData,
    onClick: (String) -> Unit
) {
    val channelDetailsEntity = notificationActionData.channelDetailsEntity
    val notificationActionType = notificationActionData.notificationActionType

    if ((notificationActionType == NotificationActionType.WITH_DROPDOWN && channelDetailsEntity.followed) ||
        notificationActionType == NotificationActionType.WITH_STATES
    ) {
        Row(
            modifier = Modifier
                .clip(getNotificationActionShape(notificationActionType))
                .background(
                    getNotificationActionBackground(
                        notificationActionType,
                        channelDetailsEntity.followed
                    )
                )
                .clickable(channelDetailsEntity.followed) {
                    onClick(channelDetailsEntity.channelId)
                }
                .padding(paddingXSmall),
            horizontalArrangement = Arrangement.spacedBy(paddingXXXSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(imageXXSmall),
                painter = painterResource(
                    id = getNotificationActionIcon(
                        notificationActionType,
                        channelDetailsEntity
                    )
                ),
                contentDescription = stringResource(id = R.string.notifications),
                colorFilter = ColorFilter.tint(
                    getNotificationActionIconTint(
                        notificationActionType,
                        channelDetailsEntity.followed
                    )
                )
            )

            if (notificationActionType == NotificationActionType.WITH_DROPDOWN) {
                Image(
                    modifier = Modifier
                        .size(imageXXSmall),
                    painter = painterResource(id = R.drawable.ic_chevron_down),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(RumbleCustomTheme.colors.primary)
                )
            }
        }
    }
}

data class NotificationActionData(
    val channelDetailsEntity: ChannelDetailsEntity,
    val notificationActionType: NotificationActionType
)

enum class NotificationActionType {
    WITH_STATES,
    WITH_DROPDOWN
}

private fun getNotificationActionShape(notificationActionType: NotificationActionType): Shape {
    return when (notificationActionType) {
        NotificationActionType.WITH_DROPDOWN -> {
            RoundedCornerShape(radiusXLarge)
        }

        NotificationActionType.WITH_STATES -> {
            CircleShape
        }
    }
}

@Composable
private fun getNotificationActionBackground(
    notificationActionType: NotificationActionType,
    followed: Boolean
): Color {
    return when (notificationActionType) {
        NotificationActionType.WITH_DROPDOWN -> {
            RumbleCustomTheme.colors.backgroundHighlight
        }

        NotificationActionType.WITH_STATES -> {
            if (followed) rumbleGreen else MaterialTheme.colors.secondaryVariant
        }
    }
}

@Composable
private fun getNotificationActionIconTint(
    notificationActionType: NotificationActionType,
    followed: Boolean
): Color {
    return when (notificationActionType) {
        NotificationActionType.WITH_DROPDOWN -> {
            RumbleCustomTheme.colors.primary
        }

        NotificationActionType.WITH_STATES -> {
            if (followed) enforcedWhite else MaterialTheme.colors.primary
        }
    }
}

@Composable
private fun getNotificationActionIcon(
    notificationActionType: NotificationActionType,
    channelDetailsEntity: ChannelDetailsEntity
): Int {
    return when (notificationActionType) {
        NotificationActionType.WITH_DROPDOWN -> {
            R.drawable.ic_notifications
        }

        NotificationActionType.WITH_STATES -> {
            if (channelDetailsEntity.followed) {
                getNotificationIcon(channelDetailsEntity)
            } else {
                R.drawable.ic_notifications_off
            }
        }
    }
}

