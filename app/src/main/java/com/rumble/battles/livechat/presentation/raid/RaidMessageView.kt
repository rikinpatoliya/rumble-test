package com.rumble.battles.livechat.presentation.raid

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rumble.battles.R
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.domain.livechat.domain.domainmodel.RaidMessageType
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography.h6Bold
import com.rumble.theme.RumbleTypography.h6Light
import com.rumble.theme.enforcedFiardHighlight
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.radiusSmall
import com.rumble.theme.raidMessageContainerHeight
import com.rumble.theme.raidMessageHeight
import com.rumble.theme.raidPirateColor
import com.rumble.theme.raidVikingColor

@Composable
fun RaidMessageView(
    modifier: Modifier = Modifier,
    type: RaidMessageType = RaidMessageType.Space,
    channelName: String = "",
    channelAvatar: String? = null,
) {
    val image = when (type) {
        RaidMessageType.Space -> painterResource(R.drawable.raid_space)
        RaidMessageType.Pirate -> painterResource(R.drawable.raid_pirate)
        RaidMessageType.Viking -> painterResource(R.drawable.raid_viking)
    }

    val backgroundColor = when (type) {
        RaidMessageType.Space -> enforcedFiardHighlight
        RaidMessageType.Pirate -> raidPirateColor
        RaidMessageType.Viking -> raidVikingColor
    }

    Box(modifier = modifier.height(raidMessageContainerHeight)) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter),
        ) {
            Box(
                modifier = Modifier
                    .height(raidMessageHeight)
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .clip(RoundedCornerShape(topStart = radiusSmall, bottomStart = radiusSmall))
                    .background(backgroundColor)
            )

            Image(
                modifier = Modifier
                    .height(raidMessageContainerHeight)
                    .align(Alignment.BottomEnd)
                    .background(MaterialTheme.colors.background),
                painter = image,
                contentDescription = "",
                contentScale = ContentScale.FillHeight
            )
        }

        Row(
            modifier = Modifier
                .padding(horizontal = paddingXSmall)
                .align(Alignment.BottomEnd)
                .height(48.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(paddingSmall)
        ) {
            ProfileImageComponent(
                profileImageComponentStyle = ProfileImageComponentStyle.CircleImageMediumStyle(),
                userName = channelName,
                userPicture = channelAvatar ?: ""
            )

            Column {
                Text(
                    text = channelName,
                    color = enforcedWhite,
                    style = h6Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = stringResource(R.string.has_raided),
                    color = enforcedWhite,
                    style = h6Light
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewSpace() {
    RumbleTheme {
        RaidMessageView(
            modifier = Modifier.fillMaxWidth(),
            type = RaidMessageType.Space,
            channelName = "Target channel",
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewPirate() {
    RumbleTheme {
        RaidMessageView(
            modifier = Modifier.fillMaxWidth(),
            type = RaidMessageType.Pirate,
            channelName = "Target channel",
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewViking() {
    RumbleTheme {
        RaidMessageView(
            modifier = Modifier.fillMaxWidth(),
            type = RaidMessageType.Viking,
            channelName = "Target channel",
        )
    }
}