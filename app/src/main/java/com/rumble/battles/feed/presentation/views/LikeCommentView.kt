package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.fierceRed
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusSmall

@Composable
fun LikeCommentView(
    modifier: Modifier = Modifier,
    likeNumber: Int = 0,
    userVote: UserVote = UserVote.NONE,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(radiusSmall))
            .clickable { onClick() },
    ) {
        Row(
            modifier = modifier.padding(
                start = paddingXSmall,
                top = paddingXXXSmall,
                end = paddingXSmall,
                bottom = paddingXXXSmall
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(paddingXXXSmall)
        ) {
            if (userVote == UserVote.LIKE) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_hart_filled),
                    contentDescription = stringResource(id = R.string.likes),
                    tint = fierceRed
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_hart),
                    contentDescription = stringResource(id = R.string.likes),
                    tint = MaterialTheme.colors.secondary
                )
            }

            Text(
                text = likeNumber.toString(),
                style = h6,
                color = MaterialTheme.colors.secondary
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    LikeCommentView(likeNumber = 100)
}

@Composable
@Preview(showBackground = true)
private fun PreviewLiked() {
    LikeCommentView(likeNumber = 100, userVote = UserVote.LIKE)
}