package com.rumble.battles.livechat.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography.h3
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.RumbleTypography.h6Light
import com.rumble.theme.imageXXSmall
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.profileItemIconContentPadding
import com.rumble.theme.radiusXMedium

enum class ModerationMenuType {
    Generic,
    Deleted,
    Self
}

@Composable
fun LiveChatModerationMenu(
    modifier: Modifier = Modifier,
    type: ModerationMenuType,
    onClose: () -> Unit,
    onMuteUser: () -> Unit,
    onDeleteMessage: () -> Unit,
    onPinMessage: () -> Unit
) {
    Box(
        modifier
            .systemBarsPadding()
            .imePadding()
            .clip(RoundedCornerShape(topStart = radiusXMedium, topEnd = radiusXMedium))
            .background(MaterialTheme.colors.onPrimary)
    ) {
        Column(modifier = Modifier.padding(top = paddingSmall, bottom = paddingLarge, end = paddingSmall)) {
            Row(
                modifier = Modifier
                    .padding(start = paddingMedium)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically) {

                Text(
                    text = stringResource(id = R.string.message_options),
                    style = h3,
                    color = MaterialTheme.colors.primary
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = onClose) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = stringResource(id = R.string.info),
                        tint = MaterialTheme.colors.primary
                    )
                }
            }

            if (type != ModerationMenuType.Self) {
                MenuItemView(
                    title = stringResource(id = R.string.mute_user),
                    text = stringResource(id = R.string.prevent_user_from_chatting),
                    leadingIcon = painterResource(id = R.drawable.ic_mute),
                    trailingIcon = painterResource(id = R.drawable.ic_arrow_right),
                    action = onMuteUser
                )
            }

            if (type != ModerationMenuType.Deleted) {
                if (type != ModerationMenuType.Self) {
                    Divider(
                        color = MaterialTheme.colors.onSurface
                    )
                }

                MenuItemView(
                    title = stringResource(id = R.string.delete_message),
                    text = stringResource(id = R.string.permanently_remove_message),
                    leadingIcon = painterResource(id = R.drawable.ic_delete_message),
                    action = onDeleteMessage
                )

                Divider(
                    color = MaterialTheme.colors.onSurface
                )

                MenuItemView(
                    title = stringResource(id = R.string.pin_message),
                    text = stringResource(id = R.string.display_message_on_top),
                    leadingIcon = painterResource(id = R.drawable.ic_pinned),
                    action = onPinMessage
                )
            }
        }
    }
}

@Composable
private fun MenuItemView(
    title: String,
    text: String,
    leadingIcon: Painter,
    trailingIcon: Painter? = null,
    action: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { action() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = paddingMedium)
                .padding(start = paddingMedium)
                .clip(CircleShape)
                .background(MaterialTheme.colors.onSurface),
        ) {
            Icon(
                modifier = Modifier
                    .padding(profileItemIconContentPadding)
                    .size(imageXXSmall),
                painter = leadingIcon,
                contentDescription = title,
                tint = MaterialTheme.colors.primary
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = paddingMedium)
        ) {
            Text(
                text = title,
                style = h4,
                color = MaterialTheme.colors.primary
            )

            Text(
                text = text,
                style = h6Light,
                color = MaterialTheme.colors.secondary
            )
        }

        trailingIcon?.let {
            Icon(
                modifier = Modifier
                    .padding(profileItemIconContentPadding)
                    .size(imageXXSmall),
                painter = it,
                contentDescription = title,
                tint = MaterialTheme.colors.secondary
            )
        }
    }
}

@Composable
@Preview
private fun Preview() {
    RumbleTheme {
        LiveChatModerationMenu(
            type = ModerationMenuType.Generic,
            onClose = {},
            onMuteUser = {},
            onDeleteMessage = {},
            onPinMessage = {}
        )
    }
}

@Composable
@Preview
private fun PreviewDark() {
    RumbleTheme(darkTheme = true) {
        LiveChatModerationMenu(
            type = ModerationMenuType.Generic,
            onClose = {},
            onMuteUser = {},
            onDeleteMessage = {},
            onPinMessage = {}
        )
    }
}