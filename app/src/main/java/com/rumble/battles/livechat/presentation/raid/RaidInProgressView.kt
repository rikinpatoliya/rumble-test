package com.rumble.battles.livechat.presentation.raid

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.battles.commonViews.ActionButton
import com.rumble.domain.livechat.domain.domainmodel.RaidEntity
import com.rumble.theme.RumbleTheme
import com.rumble.theme.borderXXSmall
import com.rumble.theme.enforcedBlack
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.radiusSmall
import com.rumble.utils.extension.consumeClick

@Composable
fun RaidInProgressView(
    modifier: Modifier = Modifier,
    raidEntity: RaidEntity,
    onJoin: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .consumeClick()
            .clip(RoundedCornerShape(radiusSmall))
            .border(
                borderXXSmall,
                MaterialTheme.colors.onSecondary,
                RoundedCornerShape(radiusSmall)
            )
            .background(MaterialTheme.colors.surface)
            .padding(paddingSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RaidTitleView(
            modifier = Modifier.weight(1f),
            raidEntity = raidEntity,
        )

        ActionButton(
            modifier = Modifier.padding(start = paddingXSmall),
            text = stringResource(R.string.join),
            onClick = onJoin,
            textColor = enforcedBlack,
            showBorder = false,
        )
    }
}

@Composable
@Preview
private fun Preview() {
    val raidEntity = RaidEntity(
        currentChannelName = "Current channel",
        targetUrl = "",
        targetChannelName = "Target channel",
        targetVideoTitle = "Video Title",
        optedOut = false
    )

    RumbleTheme {
        RaidInProgressView(
            raidEntity = raidEntity)
    }
}