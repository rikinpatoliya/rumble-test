package com.rumble.battles.livechat.presentation.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.battles.commonViews.DrawerCloseIndicatorView
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography
import com.rumble.theme.imageSmall
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.utils.extension.shortString

@Composable
fun CloseLiveChatView(
    modifier: Modifier,
    watchingNow: Long,
    pinnedMessageHidden: Boolean,
    onClose: () -> Unit,
    onShowPinnedMessage: () -> Unit
) {
    Column(modifier = modifier.background(MaterialTheme.colors.surface)) {
        DrawerCloseIndicatorView(
            modifier = Modifier.padding(
                top = paddingSmall,
                bottom = paddingMedium
            )
        )

        Row(verticalAlignment = Alignment.CenterVertically) {

            Image(
                modifier = Modifier.padding(start = paddingMedium),
                painter = painterResource(id = R.drawable.ic_dot),
                contentDescription = stringResource(id = R.string.live_chat)
            )

            Text(
                modifier = Modifier.padding(start = paddingXXXSmall, end = paddingSmall),
                text = stringResource(id = R.string.live_chat).uppercase(),
                style = RumbleTypography.h6Heavy,
                color = MaterialTheme.colors.primary,
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_view),
                contentDescription = stringResource(id = R.string.views),
                tint = MaterialTheme.colors.primary
            )

            Text(
                modifier = Modifier.padding(start = paddingXXXSmall),
                text = watchingNow.shortString(withDecimal = true),
                style = RumbleTypography.h6Light,
                color = MaterialTheme.colors.secondary
            )

            Spacer(modifier = Modifier.weight(1f))

            if (pinnedMessageHidden) {
                IconButton(
                    modifier = Modifier.size(imageSmall),
                    onClick = onShowPinnedMessage) {
                    Icon(
                        modifier = Modifier.size(imageSmall),
                        painter = painterResource(id = R.drawable.ic_pinned),
                        contentDescription = stringResource(id = R.string.close),
                        tint = MaterialTheme.colors.primary
                    )
                }
            }

            IconButton(onClick = onClose) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = stringResource(id = R.string.close),
                    tint = MaterialTheme.colors.primary
                )
            }
        }
    }
}

@Composable
@Preview
private fun Preview() {
    RumbleTheme {
        CloseLiveChatView(
            modifier = Modifier.fillMaxWidth(),
            watchingNow = 300_000,
            pinnedMessageHidden = true,
            onClose = {},
            onShowPinnedMessage = {}
        )
    }
}