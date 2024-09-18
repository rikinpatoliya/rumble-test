package com.rumble.battles.commonViews.snackbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography.body1
import com.rumble.theme.RumbleTypography.h4SemiBold
import com.rumble.theme.modalMaxWidth
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.radiusXMedium
import com.rumble.theme.snackbarCloseButtonSize
import com.rumble.theme.snackbarCloseIconSize
import com.rumble.utils.extension.clickableNoRipple


@Composable
fun RumbleSnackbar(
    modifier: Modifier = Modifier,
    snackbarData: SnackbarData,
) {
    val visuals = snackbarData.visuals
    val title = (visuals as? RumbleSnackbarVisuals)?.title

    Row(
        modifier = modifier
            .padding(horizontal = paddingSmall)
            .padding(bottom = paddingMedium)
            .widthIn(max = modalMaxWidth)
            .wrapContentHeight()
            .clickableNoRipple { /* prevent click through */ }
            .background(
                color = RumbleCustomTheme.colors.primary.copy(alpha = .9f),
                shape = RoundedCornerShape(
                    radiusXMedium
                )
            )
    ) {

        Column(modifier = Modifier.weight(1f)) {
            Spacer(modifier = Modifier.height(paddingMedium))

            if (title != null) {
                Text(
                    modifier = Modifier.padding(
                        horizontal = paddingMedium,
                    ),
                    text = title,
                    style = h4SemiBold,
                    color = RumbleCustomTheme.colors.background
                )
                Spacer(modifier = Modifier.height(paddingXSmall))
            }

            Text(
                modifier = Modifier.padding(
                    start = paddingMedium,
                    bottom = paddingMedium
                ),
                text = visuals.message,
                style = body1,
                color = RumbleCustomTheme.colors.onSecondary
            )
        }

        if (visuals.withDismissAction) {
            Box(
                modifier = Modifier
                    .padding(paddingMedium)
                    .size(snackbarCloseButtonSize, snackbarCloseButtonSize)
                    .clickableNoRipple { snackbarData.dismiss() }) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(snackbarCloseIconSize, snackbarCloseIconSize),
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = stringResource(id = R.string.close),
                    tint = RumbleCustomTheme.colors.background
                )
            }
        }

    }
}

@Preview(device = Devices.DEFAULT, showSystemUi = true)
@Composable
fun PreviewRumbleSnackbar() {
    val snackbarVisuals = RumbleSnackbarVisuals(
        title = "Unexpected crash during live stream",
        message = "Ut blandit molestie in interdum varius sed. Magnis tellus, mattis diam id tincidunt purus, id tempus in. In diam.",
        withDismissAction = true
    )

    val snackbarData = object : androidx.compose.material3.SnackbarData {
        override val visuals: SnackbarVisuals = snackbarVisuals
        override fun performAction() {}
        override fun dismiss() {}
    }

    RumbleTheme(false) {
        RumbleSnackbar(snackbarData = snackbarData)
    }
}