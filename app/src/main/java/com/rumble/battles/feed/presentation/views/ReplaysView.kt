package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusSmall
import com.rumble.theme.rumbleGreen

@Composable
fun RepliesView(
    modifier: Modifier = Modifier,
    replaysNumber: Int = 0,
    replied: Boolean = false,
    onClick: () -> Unit = {}
) {
    val tint = if (replaysNumber > 0) MaterialTheme.colors.secondary else MaterialTheme.colors.primaryVariant

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(radiusSmall))
            .clickable { onClick() },
    ) {
        Row(
            modifier = modifier
                .padding(
                    start = paddingXSmall,
                    top = paddingXXXSmall,
                    end = paddingXSmall,
                    bottom = paddingXXXSmall
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (replied) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_comments_filled),
                    tint = rumbleGreen,
                    contentDescription = pluralStringResource(
                        id = R.plurals.replies,
                        count = replaysNumber
                    )
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_comments),
                    tint = tint,
                    contentDescription = pluralStringResource(
                        id = R.plurals.replies,
                        count = replaysNumber
                    )
                )
            }

            Text(
                modifier = Modifier.padding(start = paddingXXXSmall),
                text = pluralStringResource(
                    id = R.plurals.replies,
                    count = replaysNumber,
                    replaysNumber
                ),
                color = tint
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    RepliesView(modifier = Modifier.wrapContentWidth(), replaysNumber = 10)
}

@Composable
@Preview(showBackground = true)
private fun PreviewReplied() {
    RepliesView(modifier = Modifier.wrapContentWidth(), replaysNumber = 10, replied = true)
}

@Composable
@Preview(showBackground = true)
private fun PreviewNoReplies() {
    RepliesView(modifier = Modifier.wrapContentWidth(), replaysNumber = 0)
}