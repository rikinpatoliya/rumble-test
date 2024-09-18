package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.theme.RumbleTypography.h6Heavy
import com.rumble.theme.paddingMedium

@Composable
fun CloseAddCommentView(
    modifier: Modifier,
    title: String,
    onClose: () -> Unit
) {
    Row(
        modifier = modifier.background(MaterialTheme.colors.surface),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = paddingMedium),
            text = title.uppercase(),
            style = h6Heavy
        )

        IconButton(onClick = onClose) {
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = stringResource(id = R.string.close)
            )
        }
    }
}

@Composable
@Preview
private fun Preview() {
    CloseAddCommentView(
        modifier = Modifier.fillMaxWidth(),
        title = stringResource(id = R.string.add_comment)
    ) {}
}