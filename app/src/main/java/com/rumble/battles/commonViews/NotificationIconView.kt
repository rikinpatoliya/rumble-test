package com.rumble.battles.commonViews

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.theme.borderXSmall
import com.rumble.theme.fierceRed
import com.rumble.theme.notificationDotIconSize
import com.rumble.theme.paddingXXXXSmall
import com.rumble.theme.radiusXSmall

@Composable
fun NotificationIconView(
    modifier: Modifier = Modifier,
    showDot: Boolean,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Box {
            Icon(
                painter = painterResource(id = R.drawable.ic_notifications),
                contentDescription = stringResource(id = R.string.notifications),
                modifier = Modifier
                    .padding(top = paddingXXXXSmall)
            )
            if (showDot) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_dot),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(end = paddingXXXXSmall)
                        .border(
                            borderXSmall,
                            MaterialTheme.colors.onPrimary,
                            RoundedCornerShape(radiusXSmall)
                        )
                        .size(notificationDotIconSize)
                        .align(Alignment.TopEnd),
                    tint = fierceRed
                )
            }
        }
    }
}

@Composable
@Preview
fun NotificationIconViewWithDotPreview() {
    NotificationIconView(
        showDot = true,
        onClick = {}
    )
}

@Composable
@Preview
fun NotificationIconViewWithoutDotPreview() {
    NotificationIconView(
        showDot = false,
        onClick = {}
    )
}
