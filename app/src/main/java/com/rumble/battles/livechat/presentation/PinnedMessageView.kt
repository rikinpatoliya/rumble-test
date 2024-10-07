package com.rumble.battles.livechat.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rumble.battles.R
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.domain.livechat.domain.domainmodel.BadgeEntity
import com.rumble.domain.livechat.domain.domainmodel.LiveChatMessageEntity
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography.h5
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.blueLinkColor
import com.rumble.theme.borderXXSmall
import com.rumble.theme.imageXSmall
import com.rumble.theme.imageXXSmall
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.radiusSmall
import com.rumble.utils.RumbleConstants
import com.rumble.utils.extension.consumeClick
import com.rumble.utils.getUrlAnnotatedString
import java.time.LocalDateTime

@Composable
fun PinnedMessageView(
    modifier: Modifier = Modifier,
    message: LiveChatMessageEntity,
    badges: Map<String, BadgeEntity> = emptyMap(),
    canModerate: Boolean,
    onUnpin: () -> Unit,
    onHide: () -> Unit,
    onLinkClick: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .consumeClick()
            .clip(RoundedCornerShape(radiusSmall))
            .border(
                borderXXSmall,
                MaterialTheme.colors.onSecondary,
                RoundedCornerShape(radiusSmall)
            )
            .background(MaterialTheme.colors.surface)
            .padding(paddingSmall)

    ) {
        Column(verticalArrangement = Arrangement.spacedBy(paddingSmall)) {
            PinnedMessageHeader(
                isExpanded = isExpanded,
                canModerate = canModerate,
                onCollapse = { isExpanded = false },
                onExpend = { isExpanded = true },
                onUnpin = onUnpin,
                onHide = onHide
            )
            val annotatedText =
                getUrlAnnotatedString(AnnotatedString(message.message), blueLinkColor)
            ClickableText(
                text = annotatedText,
                style = h5.copy(color = MaterialTheme.colors.primary),
                maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                overflow = TextOverflow.Ellipsis,
                onClick = { offset ->
                    val result = annotatedText
                        .getStringAnnotations(RumbleConstants.TAG_URL, offset, offset)
                        .firstOrNull()
                    if (result != null) {
                        onLinkClick(result.item)
                    }
                }
            )

            PinnedMessageFooter(
                message = message,
                badges = badges,
                onLinkClick = onLinkClick
            )
        }
    }
}

@Composable
private fun PinnedMessageHeader(
    isExpanded: Boolean,
    canModerate: Boolean,
    onExpend: () -> Unit,
    onCollapse: () -> Unit,
    onUnpin: () -> Unit,
    onHide: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .padding(end = paddingXSmall)
                .size(imageXXSmall),
            painter = painterResource(id = R.drawable.ic_pinned),
            contentDescription = stringResource(id = R.string.pinned_message),
            tint = MaterialTheme.colors.secondary
        )

        Text(
            text = stringResource(id = R.string.pinned_message),
            style = h6,
            color = MaterialTheme.colors.secondary
        )

        Spacer(modifier = Modifier.weight(1f))

        if (canModerate) {
            IconButton(
                modifier = Modifier.size(imageXSmall),
                onClick = onUnpin
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_hide_pinned),
                    contentDescription = stringResource(id = R.string.unpin_message),
                    tint = MaterialTheme.colors.primary
                )
            }
        }

        IconButton(
            modifier = Modifier
                .padding(horizontal = paddingSmall)
                .size(imageXSmall),
            onClick = onHide
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_visible_off),
                contentDescription = stringResource(id = R.string.hide_pinned_message),
                tint = MaterialTheme.colors.primary
            )
        }

        if (isExpanded) {
            IconButton(
                modifier = Modifier.size(imageXSmall),
                onClick = onCollapse
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_up),
                    contentDescription = stringResource(id = R.string.hide_pinned_message),
                    tint = MaterialTheme.colors.primary
                )
            }
        } else {
            IconButton(
                modifier = Modifier.size(imageXSmall),
                onClick = onExpend
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_down),
                    contentDescription = stringResource(id = R.string.hide_pinned_message),
                    tint = MaterialTheme.colors.primary
                )
            }
        }
    }
}

@Composable
private fun PinnedMessageFooter(
    message: LiveChatMessageEntity,
    badges: Map<String, BadgeEntity>,
    onLinkClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfileImageComponent(
            modifier = Modifier.padding(end = paddingXSmall),
            profileImageComponentStyle = ProfileImageComponentStyle.CircleImageXSmallStyle(),
            userName = message.userName,
            userPicture = message.userThumbnail ?: ""
        )

        LiveChatContentView(
            userName = message.userName,
            userBadges = message.badges,
            badges = badges,
            atMentionRange = message.atMentionRange,
            userNameColor = message.userNameColor ?: MaterialTheme.colors.primary
        )
    }
}

@Composable
@Preview
private fun Preview() {
    val message = LiveChatMessageEntity(
        message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        badges = emptyList(),
        currencySymbol = "$",
        messageId = 0,
        timeReceived = LocalDateTime.now(),
        userId = 1L,
        userName = "Test user",
        userThumbnail = null
    )
    RumbleTheme {
        PinnedMessageView(
            modifier = Modifier.width(400.dp),
            message = message,
            canModerate = false,
            onUnpin = {},
            onHide = {},
            onLinkClick = {}
        )
    }
}

