package com.rumble.battles.livechat.presentation.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.rumble.battles.R
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.domain.livechat.domain.domainmodel.BadgeEntity
import com.rumble.domain.livechat.domain.domainmodel.LiveChatMessageEntity
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXXSmall
import com.rumble.theme.radiusSmall
import com.rumble.utils.extension.clickableNoRipple
import com.rumble.utils.extension.conditional
import java.math.BigDecimal
import java.time.LocalDateTime

@Composable
fun DeletedMessageView(
    modifier: Modifier = Modifier,
    messageEntity: LiveChatMessageEntity,
    badges: Map<String, BadgeEntity>,
    onClick: (LiveChatMessageEntity) -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radiusSmall))
            .background(messageEntity.background?.copy(0.1f) ?: MaterialTheme.colors.background)
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (image, name, message, cover) = createRefs()

            ProfileImageComponent(
                modifier = Modifier
                    .conditional(messageEntity.background != null) {
                        Modifier.padding(
                            start = paddingXSmall,
                            top = paddingSmall
                        )
                    }
                    .conditional(messageEntity.background == null) {
                        Modifier.padding(start = paddingXXXXSmall)
                    }
                    .constrainAs(image) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    },
                profileImageComponentStyle = ProfileImageComponentStyle.CircleImageXSmallStyle(),
                userName = messageEntity.userName,
                userPicture = messageEntity.userThumbnail ?: ""
            )

            LiveChatContentView(modifier = Modifier
                .clickableNoRipple { onClick(messageEntity) }
                .padding(horizontal = paddingXSmall)
                .conditional(messageEntity.background != null) {
                    Modifier.padding(top = paddingXSmall)
                }
                .constrainAs(name) {
                    top.linkTo(parent.top)
                    start.linkTo(image.end)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
                userName = messageEntity.userName,
                userBadges = messageEntity.badges,
                badges = badges,
                userNameColor = messageEntity.userNameColor ?: MaterialTheme.colors.primary
            )

            Text(
                modifier = Modifier
                    .padding(start = paddingXSmall)
                    .constrainAs(message) {
                        top.linkTo(name.bottom)
                        start.linkTo(image.end)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    },
                text = stringResource(id = R.string.live_chat_deleted_message),
                style = RumbleTypography.h6LightItalic
            )

            Box(
                modifier = Modifier
                    .clickableNoRipple { onClick(messageEntity) }
                    .constrainAs(cover) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                    .background(MaterialTheme.colors.background.copy(0.5f))
            )
        }
    }
}

@Composable
@Preview
private fun Preview() {
    RumbleTheme(darkTheme = false) {
        DeletedMessageView(
            modifier = Modifier.fillMaxWidth(),
            messageEntity = LiveChatMessageEntity(
                messageId = 0,
                userId = 0,
                userName = "JennyWilson",
                badges = emptyList(),
                message = "",
                timeReceived = LocalDateTime.now(),
                userThumbnail = null,
                rantPrice = BigDecimal.TEN,
                currencySymbol = "",
                deleted = true
            ),
            badges = emptyMap(),
            onClick = {}
        )
    }
}
