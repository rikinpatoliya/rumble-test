package com.rumble.battles.livechat.presentation.rant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.battles.livechat.presentation.content.LiveChatUserNameView
import com.rumble.battles.livechat.presentation.content.MessageContentView
import com.rumble.domain.livechat.domain.domainmodel.BadgeEntity
import com.rumble.domain.livechat.domain.domainmodel.LiveChatConfig
import com.rumble.domain.livechat.domain.domainmodel.LiveChatMessageEntity
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography.h6Bold
import com.rumble.theme.RumbleTypography.h6Light
import com.rumble.theme.RumbleTypography.h6LightItalic
import com.rumble.theme.darkFierceRed
import com.rumble.theme.fierceRed
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusSmall
import com.rumble.theme.rantCloseButtonSize
import com.rumble.utils.extension.conditional
import java.math.BigDecimal
import java.time.LocalDateTime

@Composable
fun RantMessageView(
    modifier: Modifier = Modifier,
    messageEntity: LiveChatMessageEntity,
    badges: Map<String, BadgeEntity>,
    onDismiss: (() -> Unit)? = null,
    liveChatConfig: LiveChatConfig?,
    scrollable: Boolean = false,
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier.clip(RoundedCornerShape(radiusSmall))
    ) {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(messageEntity.titleBackground ?: darkFierceRed)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    ProfileImageComponent(
                        modifier = Modifier.padding(start = paddingXSmall, top = paddingXSmall),
                        profileImageComponentStyle = ProfileImageComponentStyle.CircleImageXSmallStyle(),
                        userName = messageEntity.userName,
                        userPicture = messageEntity.userThumbnail ?: ""
                    )

                    LiveChatUserNameView(
                        modifier = Modifier
                            .weight(1f)
                            .padding(
                                start = paddingXXXSmall,
                                bottom = paddingXXSmall,
                                end = paddingXXXSmall,
                                top = paddingXSmall
                            ),
                        userName = messageEntity.userName,
                        userBadges = messageEntity.badges,
                        badges = badges,
                        rantPrice = messageEntity.rantPrice,
                        currencySymbol = messageEntity.currencySymbol,
                        textStyle = h6Bold,
                        textColor = messageEntity.textColor ?: MaterialTheme.colors.primary
                    )

                    onDismiss?.let {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                modifier = Modifier.size(rantCloseButtonSize),
                                painter = painterResource(id = R.drawable.ic_close),
                                contentDescription = stringResource(id = R.string.close),
                                tint = messageEntity.textColor ?: MaterialTheme.colors.primary
                            )
                        }
                    }
                }
            }

            if (messageEntity.deleted) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(messageEntity.background ?: fierceRed)
                        .padding(
                            start = paddingXSmall,
                            bottom = paddingXSmall,
                            top = paddingXSmall,
                            end = paddingXSmall
                        ),
                    text = stringResource(id = R.string.live_chat_deleted_message),
                    style = h6LightItalic,
                    color = messageEntity.textColor ?: MaterialTheme.colors.primary
                )
            } else {
                Column(
                    modifier = Modifier.conditional(scrollable) {
                        verticalScroll(scrollState)
                    }
                ) {
                    MessageContentView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(messageEntity.background ?: fierceRed)
                            .padding(
                                start = paddingXSmall,
                                bottom = paddingXSmall,
                                top = paddingXSmall,
                                end = paddingXSmall
                            ),
                        messageEntity = messageEntity,
                        liveChatConfig = liveChatConfig,
                        style = h6Light,
                        color = messageEntity.textColor ?: MaterialTheme.colors.primary,
                        atTextColor = messageEntity.textColor ?: MaterialTheme.colors.primary,
                        atHighlightColor = Color.White.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun Preview() {
    RumbleTheme {
        RantMessageView(
            modifier = Modifier.fillMaxWidth(),
            messageEntity = LiveChatMessageEntity(
                messageId = 0,
                userId = 0,
                userName = "Test user name",
                badges = emptyList(),
                message = "Some test message",
                timeReceived = LocalDateTime.now(),
                userThumbnail = null,
                rantPrice = BigDecimal.TEN,
                background = Color.LightGray,
                titleBackground = Color.Gray,
                currencySymbol = ""
            ),
            badges = emptyMap(),
            onDismiss = {},
            liveChatConfig = null
        )
    }
}

@Composable
@Preview
private fun PreviewDeleted() {
    RumbleTheme {
        RantMessageView(
            modifier = Modifier.fillMaxWidth(),
            messageEntity = LiveChatMessageEntity(
                messageId = 0,
                userId = 0,
                userName = "Test user name",
                badges = emptyList(),
                message = "Some test message",
                timeReceived = LocalDateTime.now(),
                userThumbnail = null,
                rantPrice = BigDecimal.TEN,
                background = Color.LightGray,
                titleBackground = Color.Gray,
                currencySymbol = "",
                deleted = true
            ),
            badges = emptyMap(),
            onDismiss = {},
            liveChatConfig = null
        )
    }
}

@Composable
@Preview
private fun PreviewNoBackgroundSet() {
    RumbleTheme {
        RantMessageView(
            modifier = Modifier.fillMaxWidth(),
            messageEntity = LiveChatMessageEntity(
                messageId = 0,
                userId = 0,
                userName = "Test user name",
                badges = emptyList(),
                message = "Some test message",
                timeReceived = LocalDateTime.now(),
                userThumbnail = null,
                rantPrice = BigDecimal.TEN,
                background = null,
                titleBackground = null,
                currencySymbol = ""
            ),
            badges = emptyMap(),
            onDismiss = {},
            liveChatConfig = null
        )
    }
}
