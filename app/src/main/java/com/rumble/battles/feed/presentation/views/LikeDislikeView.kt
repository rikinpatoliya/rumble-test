package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import com.rumble.theme.RumbleTheme
import com.rumble.theme.borderXXSmall
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXXSmall

enum class LikeDislikeViewStyle {
    Compact,
    Normal,
    ActionButtonsWithBarBelow,
}

@Composable
fun LikeDislikeView(
    modifier: Modifier = Modifier,
    style: LikeDislikeViewStyle = LikeDislikeViewStyle.Compact,
    likeNumber: Long,
    dislikeNumber: Long,
    userVote: UserVote,
    onLike: () -> Unit,
    onDislike: () -> Unit,
    enabled: Boolean = true,
) {
    when (style) {
        LikeDislikeViewStyle.Compact -> {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LikeView(
                    modifier = Modifier.padding(end = paddingXSmall),
                    style = style,
                    enabled = enabled,
                    likeNumber = likeNumber,
                    userVote = userVote
                ) { onLike() }
                LikeDislikeBar(
                    modifier = Modifier.weight(1f),
                    style = style,
                    likeNumber = likeNumber, dislikeNumber = dislikeNumber
                )
                DislikeView(
                    modifier = Modifier.padding(start = paddingXSmall),
                    style = style,
                    enabled = enabled,
                    dislikeNumber = dislikeNumber,
                    userVote = userVote
                ) { onDislike() }
            }
        }

        LikeDislikeViewStyle.ActionButtonsWithBarBelow -> {
            var siblingWidth by remember { mutableIntStateOf(0) }
            val density = LocalContext.current.resources.displayMetrics.density
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .onGloballyPositioned {
                            siblingWidth = it.size.width
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(borderXXSmall),
                ) {
                    LikeView(
                        style = style,
                        enabled = enabled,
                        likeNumber = likeNumber,
                        userVote = userVote
                    ) { onLike() }
                    DislikeView(
                        style = style,
                        enabled = enabled,
                        dislikeNumber = dislikeNumber,
                        userVote = userVote
                    ) { onDislike() }
                }
                LikeDislikeBar(
                    modifier = Modifier
                        .padding(
                            top = paddingXXXXSmall,
                        )
                        .width((siblingWidth / density).dp),
                    style = LikeDislikeViewStyle.ActionButtonsWithBarBelow,
                    likeNumber = likeNumber,
                    dislikeNumber = dislikeNumber
                )
            }
        }

        else -> {
            ConstraintLayout(modifier = modifier) {
                val (likeView, dislikeView, bar) = createRefs()
                LikeView(
                    modifier = Modifier
                        .padding(end = paddingXSmall)
                        .constrainAs(likeView) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                        },
                    style = style,
                    enabled = enabled,
                    likeNumber = likeNumber,
                    userVote = userVote
                ) { onLike() }
                DislikeView(
                    modifier = Modifier
                        .padding(start = paddingXSmall)
                        .constrainAs(dislikeView) {
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                        },
                    style = style,
                    enabled = enabled,
                    dislikeNumber = dislikeNumber,
                    userVote = userVote
                ) { onDislike() }
                LikeDislikeBar(
                    modifier = Modifier
                        .padding(top = paddingSmall)
                        .constrainAs(bar) {
                            start.linkTo(likeView.end)
                            end.linkTo(dislikeView.start)
                            top.linkTo(likeView.top)
                            width = Dimension.fillToConstraints
                        },
                    likeNumber = likeNumber,
                    dislikeNumber = dislikeNumber
                )
            }
        }
    }
}

@Composable
@Preview
private fun PreviewNormal() {
    LikeDislikeView(
        modifier = Modifier.fillMaxWidth(),
        style = LikeDislikeViewStyle.Normal,
        likeNumber = 554_000,
        dislikeNumber = 1,
        userVote = UserVote.DISLIKE,
        onLike = {},
        onDislike = {})
}

@Composable
@Preview
private fun PreviewCompact() {
    LikeDislikeView(
        modifier = Modifier.fillMaxWidth(),
        style = LikeDislikeViewStyle.Compact,
        likeNumber = 20,
        dislikeNumber = 10,
        userVote = UserVote.DISLIKE,
        onLike = {},
        onDislike = {})
}

@Composable
@Preview
private fun PreviewWithBarBelow() {
    RumbleTheme {
        LikeDislikeView(
            modifier = Modifier.fillMaxWidth(),
            style = LikeDislikeViewStyle.ActionButtonsWithBarBelow,
            likeNumber = 20_000_000,
            dislikeNumber = 10_000,
            userVote = UserVote.NONE,
            onLike = {},
            onDislike = {})
    }
}
