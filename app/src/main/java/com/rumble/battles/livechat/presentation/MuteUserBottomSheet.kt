package com.rumble.battles.livechat.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.battles.commonViews.BottomSheetItem
import com.rumble.battles.commonViews.RumbleBottomSheet
import com.rumble.domain.livechat.domain.domainmodel.MutePeriod
import com.rumble.theme.RumbleTheme

@Composable
fun MuteUserBottomSheet(
    userName: String,
    onMute: (MutePeriod) -> Unit,
    onCancel: () -> Unit
) {
    RumbleBottomSheet(
        title = stringResource(id = R.string.mute_user_title),
        subtitle = userName,
        sheetItems = listOf(
            BottomSheetItem(
                text = stringResource(id = R.string.mute_five_minute)
            ) {
                onMute(MutePeriod.FiveMinutes)
            },
            BottomSheetItem(
                text = stringResource(id = R.string.mute_for_livestream)
            ) {
                onMute(MutePeriod.LiveStreamDuration)
            },
            BottomSheetItem(
                text = stringResource(id = R.string.forever)
            ) {
                onMute(MutePeriod.Forever)
            },
        ),
        onCancel = onCancel
    )
}

@Composable
@Preview
private fun Preview() {
    RumbleTheme {
        MuteUserBottomSheet(
            userName = "Test user name",
            onMute = { _ -> },
            onCancel = {}
        )
    }
}