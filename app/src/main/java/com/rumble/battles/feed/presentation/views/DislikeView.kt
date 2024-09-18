package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.DislikeVideTag
import com.rumble.battles.R
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography
import com.rumble.theme.enforcedFiord
import com.rumble.theme.enforcedWhite
import com.rumble.theme.likeDislikeWidth
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.paddingXXXXSmall
import com.rumble.theme.radiusLarge
import com.rumble.theme.roundControlSize
import com.rumble.utils.extension.clickableNoRipple
import com.rumble.utils.extension.conditional
import com.rumble.utils.extension.shortString

@Composable
fun DislikeView(
    modifier: Modifier = Modifier,
    style: LikeDislikeViewStyle = LikeDislikeViewStyle.Compact,
    enabled: Boolean = true,
    dislikeNumber: Long,
    userVote: UserVote,
    onClick: () -> Unit
) {
    val currentBackgroundColor = if (enabled) getBackgroundColor(userVote) else getBackgroundColor(userVote).copy(alpha = 0.5f)
    val currentIconColor = if (enabled) getIconColor(userVote) else getIconColor(userVote).copy(alpha = 0.5f)
    val currentTextColor = if (enabled) getTextColor(userVote) else getTextColor(userVote).copy(alpha = 0.5f)

    when (style) {
        LikeDislikeViewStyle.Compact -> {
            Box(
                modifier = modifier
                    .semantics { testTag = DislikeVideTag }
                    .clip(RoundedCornerShape(radiusLarge))
                    .background(currentBackgroundColor)
                    .conditional(enabled) { clickable { onClick() } }
            ) {
                Row(
                    modifier = Modifier
                        .width(likeDislikeWidth)
                        .padding(
                            top = paddingXXXSmall,
                            bottom = paddingXXXSmall
                        ),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        painter = painterResource(id = R.drawable.ic_thumb_down),
                        contentDescription = stringResource(id = R.string.likes),
                        tint = currentIconColor
                    )

                    Text(
                        modifier = Modifier.padding(start = paddingXXXSmall),
                        text = dislikeNumber.shortString(false),
                        style = RumbleTypography.h6Heavy,
                        color = currentTextColor
                    )
                }
            }
        }

        LikeDislikeViewStyle.ActionButtonsWithBarBelow -> {
            Box(
                modifier = modifier
                    .width(likeDislikeWidth)
                    .semantics { testTag = DislikeVideTag }
                    .background(currentBackgroundColor)
                    .conditional(enabled) { clickable { onClick() } }
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(
                            top = paddingXXXSmall,
                            bottom = paddingXXXXSmall,
                            start = paddingXSmall,
                            end = paddingSmall
                        ),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        painter = painterResource(id = R.drawable.ic_thumb_down),
                        contentDescription = stringResource(id = R.string.likes),
                        tint = currentIconColor
                    )

                    Text(
                        modifier = Modifier.padding(start = paddingXXXSmall),
                        text = dislikeNumber.shortString(false),
                        style = RumbleTypography.h6,
                        color = currentTextColor
                    )
                }
            }
        }

        else -> {
            Column(
                modifier = modifier
                    .semantics { testTag = DislikeVideTag },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(roundControlSize)
                        .clip(CircleShape)
                        .background(currentBackgroundColor)
                        .conditional(enabled) { clickable { onClick() } }
                ) {
                    Icon(
                        modifier = Modifier.align(Alignment.Center),
                        painter = painterResource(id = R.drawable.ic_thumb_down),
                        contentDescription = stringResource(id = R.string.likes),
                        tint = currentIconColor
                    )
                }
                Text(
                    text = dislikeNumber.shortString(false),
                    modifier = Modifier.clickableNoRipple { onClick() },
                    style = RumbleTypography.h6,
                    color = MaterialTheme.colors.secondary
                )
            }
        }
    }
}

@Composable
private fun getIconColor(userVote: UserVote): Color =
    if (userVote == UserVote.DISLIKE) MaterialTheme.colors.onPrimary else MaterialTheme.colors.primaryVariant

@Composable
private fun getBackgroundColor(userVote: UserVote) =
    if (userVote == UserVote.DISLIKE) MaterialTheme.colors.secondary else MaterialTheme.colors.onSecondary

@Composable
private fun getTextColor(userVote: UserVote) =
    if (userVote == UserVote.DISLIKE) MaterialTheme.colors.onPrimary else if (MaterialTheme.colors.isLight) enforcedFiord else enforcedWhite

@Composable
@Preview
private fun PreviewCompact() {
    RumbleTheme {
        DislikeView(dislikeNumber = 123_001, userVote = UserVote.DISLIKE) {}
    }
}

@Composable
@Preview
private fun PreviewNormal() {
    RumbleTheme {
        DislikeView(
            style = LikeDislikeViewStyle.Normal,
            dislikeNumber = 123_001,
            userVote = UserVote.DISLIKE
        ) {}
    }
}

@Composable
@Preview
private fun PreviewActionButtonsWithBarBelow() {
    RumbleTheme {
        DislikeView(
            style = LikeDislikeViewStyle.ActionButtonsWithBarBelow,
            dislikeNumber = 123_001,
            userVote = UserVote.DISLIKE
        ) {}
    }
}
