package com.rumble.battles.livechat.presentation.emoji

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.domain.livechat.domain.domainmodel.EmoteEntity
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography.body1
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.RumbleTypography.h6Light
import com.rumble.theme.imageXMedium
import com.rumble.theme.imageXXSmall
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXSmall10

@Composable
fun EmoteRequestSubscribeView(
    modifier: Modifier = Modifier,
    channelName: String = "",
    emoteEntity: EmoteEntity? = null,
    onBack: () -> Unit = {},
) {
    Column (
        modifier = modifier
            .background(RumbleCustomTheme.colors.background)
    ) {
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .clickable { onBack() }
                .padding(paddingSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(imageXXSmall),
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.back),
                tint = RumbleCustomTheme.colors.primary,
            )

            Text(
                text = stringResource(id = R.string.back),
                style = h6
            )
        }

        Row(
            modifier = Modifier
                .padding(paddingSmall)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(paddingXSmall10)
        ) {
            emoteEntity?.let {
                EmoteImageView(
                    emoteSize = imageXMedium,
                    emoteEntity = emoteEntity,
                    onClick = {},
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(paddingXSmall)) {
                Text(
                    text = stringResource(R.string.subscriber_exclusive_emote, channelName),
                    style = h4,
                    color = RumbleCustomTheme.colors.primary,
                )

                Text(
                    text = ":${emoteEntity?.name ?: ""}:",
                    style = body1,
                    color = RumbleCustomTheme.colors.secondary,
                )

                Text(
                    text = stringResource(R.string.must_be_supporter),
                    style = h6Light,
                    color = RumbleCustomTheme.colors.secondary,
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    RumbleTheme {
        EmoteRequestSubscribeView(
            channelName = "Test channel name",
            emoteEntity = EmoteEntity(
                name = ":test emote:",
                url = "",
                subscribersOnly = false,
            ),
        )
    }
}