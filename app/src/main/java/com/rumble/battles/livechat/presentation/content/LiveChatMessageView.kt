package com.rumble.battles.livechat.presentation.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.domain.livechat.domain.domainmodel.BadgeEntity
import com.rumble.domain.livechat.domain.domainmodel.LiveChatConfig
import com.rumble.domain.livechat.domain.domainmodel.LiveChatMessageEntity
import com.rumble.theme.RumbleTheme
import com.rumble.theme.highlightRed
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.paddingXXXXSmall
import com.rumble.theme.radiusMedium
import com.rumble.utils.RumbleConstants.BADGE_RECURRING_SUBSCRIPTION
import com.rumble.utils.extension.conditional
import java.time.LocalDateTime

@Composable
fun LiveChatMessageView(
    modifier: Modifier = Modifier,
    messageEntity: LiveChatMessageEntity,
    badges: Map<String, BadgeEntity>,
    liveChatConfig: LiveChatConfig?,
    onClick: (LiveChatMessageEntity) -> Unit,
    onLinkClick: (String) -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radiusMedium))
            .conditional(messageEntity.badges.contains(BADGE_RECURRING_SUBSCRIPTION)) {
                background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            highlightRed.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
            }
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (icon, content) = createRefs()

            ProfileImageComponent(
                modifier = Modifier
                    .constrainAs(icon) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    }
                    .padding(vertical = paddingXXXXSmall)
                    .padding(
                        start = paddingXXXXSmall,
                        end = paddingXSmall
                    ),
                profileImageComponentStyle = ProfileImageComponentStyle.CircleImageXSmallStyle(),
                userName = messageEntity.userName,
                userPicture = messageEntity.userThumbnail ?: ""
            )

            LiveChatContentView(modifier = Modifier
                .constrainAs(content) {
                    top.linkTo(icon.top)
                    start.linkTo(icon.end)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .padding(vertical = paddingXXXSmall),
                liveChatConfig = liveChatConfig,
                message = messageEntity.message,
                userName = messageEntity.userName,
                userBadges = messageEntity.badges,
                badges = badges,
                atMentionRange = messageEntity.atMentionRange,
                userNameColor = messageEntity.userNameColor ?: MaterialTheme.colors.primary,
                onLinkClick = onLinkClick,
                onClick = {
                    onClick(messageEntity)
                }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    RumbleTheme {
        LiveChatMessageView(
            modifier = Modifier.fillMaxWidth(),
            messageEntity = LiveChatMessageEntity(
                messageId = 0,
                userId = 0,
                userName = "Test user name",
                badges = emptyList(),
                message = "Some test message",
                timeReceived = LocalDateTime.now(),
                userThumbnail = null,
                rantPrice = null,
                currencySymbol = ""
            ),
            badges = emptyMap(),
            liveChatConfig = null,
            onClick = {},
            onLinkClick = {}
        )
    }
}
