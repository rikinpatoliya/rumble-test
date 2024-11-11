package com.rumble.battles.livechat.presentation.raid

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.battles.commonViews.ActionButton
import com.rumble.domain.livechat.domain.domainmodel.RaidEntity
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography.h3
import com.rumble.theme.borderXXSmall
import com.rumble.theme.enforcedBlack
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.progressBarHeight
import com.rumble.theme.radiusSmall
import com.rumble.theme.rumbleGreen
import com.rumble.utils.extension.consumeClick

@Composable
fun RaidPopupView(
    modifier: Modifier = Modifier,
    raidEntity: RaidEntity,
    onJoin: () -> Unit = {},
    onOptOut: () -> Unit = {},
) {
    val progress = raidEntity.timePassed.toFloat() / raidEntity.timeOut.toFloat()
    val timeToRaid = raidEntity.timeOut - raidEntity.timePassed

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
    ) {
        Column(
            modifier = modifier
                .consumeClick()
                .padding(paddingSmall),
            verticalArrangement = Arrangement.spacedBy(paddingXXXSmall)

        ) {
            RaidTitleView(
                modifier = Modifier.fillMaxWidth(),
                raidEntity = raidEntity,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.raid_starting_in, timeToRaid),
                    style = h3,
                    color = RumbleCustomTheme.colors.primary
                )

                Spacer(modifier = Modifier.weight(1f))

                if (raidEntity.optedOut) {
                    ActionButton(
                        text = stringResource(R.string.join),
                        onClick = onJoin,
                        textColor = enforcedBlack,
                        showBorder = false,
                    )
                } else {
                    ActionButton(
                        text = stringResource(R.string.opt_out),
                        onClick = onOptOut,
                        textColor = RumbleCustomTheme.colors.primary,
                        backgroundColor = RumbleCustomTheme.colors.backgroundHighlight,
                        showBorder = false,
                    )
                }
            }
        }

        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(progressBarHeight),
            progress = progress,
            color = rumbleGreen,
            backgroundColor = RumbleCustomTheme.colors.backgroundHighlight
        )
    }
}

@Composable
@Preview
private fun PreviewJoin() {
    val raidEntity = RaidEntity(
        currentChannelName = "Current channel",
        targetUrl = "",
        targetChannelName = "Target channel",
        targetVideoTitle = "Video Title",
        timeOut = 5,
        timePassed = 3,
    )

    RumbleTheme {
        RaidPopupView(raidEntity = raidEntity)
    }
}

@Composable
@Preview
private fun PreviewOptOut() {
    val raidEntity = RaidEntity(
        currentChannelName = "Current channel",
        targetUrl = "",
        targetChannelName = "Target channel",
        targetVideoTitle = "Video Title",
        optedOut = false
    )

    RumbleTheme {
        RaidPopupView(raidEntity = raidEntity)
    }
}