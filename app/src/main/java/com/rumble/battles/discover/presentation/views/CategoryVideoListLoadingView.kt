package com.rumble.battles.discover.presentation.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.theme.RumbleTypography
import com.rumble.theme.paddingMedium

@Composable
@Preview(showBackground = true)
fun CategoryVideoListLoadingView(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Image(
            modifier = Modifier.fillMaxWidth(),
            painter = painterResource(id = R.drawable.video_card_ghost),
            contentDescription = "",
            contentScale = ContentScale.FillWidth,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.secondaryVariant)
        )

        Text(
            modifier = Modifier.padding(vertical = paddingMedium),
            text = stringResource(id = R.string.recommended_categories).uppercase(),
            style = RumbleTypography.h4
        )

        Image(
            modifier = Modifier.fillMaxWidth(),
            painter = painterResource(id = R.drawable.live_categories_ghost),
            contentDescription = "",
            contentScale = ContentScale.FillWidth,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.secondaryVariant)
        )

        Image(
            modifier = Modifier.fillMaxWidth().padding(top = paddingMedium),
            painter = painterResource(id = R.drawable.video_card_ghost),
            contentDescription = "",
            contentScale = ContentScale.FillWidth,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.secondaryVariant)
        )
    }
}