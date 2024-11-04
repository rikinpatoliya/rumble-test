package com.rumble.battles.livechat.presentation.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.domain.livechat.domain.domainmodel.BadgeEntity
import com.rumble.domain.livechat.domain.domainmodel.LiveChatConfig
import com.rumble.domain.livechat.domain.domainmodel.LiveChatMessageEntity
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography.h6Bold
import com.rumble.theme.RumbleTypography.h6Light
import com.rumble.theme.brandedLocalsRed
import com.rumble.theme.enforcedWhite
import com.rumble.theme.imageSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.paddingXXXXSmall
import com.rumble.theme.radiusMedium
import com.rumble.theme.rumbleGreen
import com.rumble.theme.wokeGreen
import com.rumble.utils.extension.conditional
import java.time.LocalDateTime

@Composable
fun LiveChatNotificationView(
    modifier: Modifier = Modifier,
    messageEntity: LiveChatMessageEntity,
    badges: Map<String, BadgeEntity>,
    liveChatConfig: LiveChatConfig?
) {
    val badge =  badges[messageEntity.notificationBadge]

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radiusMedium))
            .background(brandedLocalsRed)
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (image, name, message, notificationBadge) = createRefs()

            ProfileImageComponent(
                modifier = Modifier
                    .padding(
                        start = paddingXXXSmall,
                        top = paddingXXXSmall
                    )
                    .constrainAs(image) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    },
                profileImageComponentStyle = ProfileImageComponentStyle.CircleImageXSmallStyle(),
                userName = messageEntity.userName,
                userPicture = messageEntity.userThumbnail ?: ""
            )

            LiveChatUserNameView(
                modifier = Modifier
                    .padding(
                        start = paddingXXXSmall,
                        end = paddingXSmall,
                        top = paddingXXXXSmall,
                    )
                    .constrainAs(name) {
                        top.linkTo(image.top)
                        start.linkTo(image.end)
                        if (badge == null) end.linkTo(parent.end)
                        else end.linkTo(notificationBadge.start)
                        width = Dimension.fillToConstraints
                    },
                userName = messageEntity.userName,
                userBadges = messageEntity.badges,
                badges = badges,
                rantPrice = messageEntity.rantPrice,
                currencySymbol = messageEntity.currencySymbol,
                textColor = enforcedWhite,
                textStyle = h6Bold
            )

            MessageContentView(
                modifier = Modifier
                    .padding(
                        start = paddingXXXSmall,
                        bottom = paddingXXXSmall
                    )
                    .conditional(messageEntity.background != null) {
                        Modifier.padding(end = paddingXSmall)
                    }
                    .constrainAs(message) {
                        top.linkTo(name.bottom)
                        start.linkTo(image.end)
                        if (badge == null) end.linkTo(parent.end)
                        else end.linkTo(notificationBadge.start)
                        width = Dimension.fillToConstraints
                    },
                messageEntity = messageEntity,
                liveChatConfig = liveChatConfig,
                style = h6Light,
                color = enforcedWhite,
                atTextColor = if (MaterialTheme.colors.isLight) wokeGreen else rumbleGreen,
                atHighlightColor = rumbleGreen.copy(alpha = 0.2f)
            )

           badge?.let {
                AsyncImage(
                    modifier = Modifier
                        .padding(end = paddingXSmall)
                        .constrainAs(notificationBadge) {
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                        .size(imageSmall),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(it.url)
                        .build(),
                    contentDescription = it.label,
                    contentScale = ContentScale.FillHeight
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewNotification() {
    RumbleTheme {
        LiveChatNotificationView(
            modifier = Modifier.fillMaxWidth(),
            messageEntity = LiveChatMessageEntity(
                messageId = 0,
                userId = 0,
                userName = "Test user name",
                badges = emptyList(),
                message = "Some test message",
                timeReceived = LocalDateTime.now(),
                userThumbnail = null,
                currencySymbol = "",
                isNotification = true,
                notification = "Notification text",
                notificationBadge = "Test badge"
            ),
            badges = emptyMap(),
            liveChatConfig = null
        )
    }
}
