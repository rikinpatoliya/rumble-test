package com.rumble.battles.search.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.theme.RumbleTypography.h5
import com.rumble.theme.paddingMedium
import com.rumble.theme.radiusXLarge

@Composable
fun SearchQueryView(
    modifier: Modifier = Modifier,
    query: String
) {
    Box(modifier = modifier
        .clip(RoundedCornerShape(radiusXLarge))
        .background(MaterialTheme.colors.onSecondary)) {
        Text(
            modifier = Modifier.padding(paddingMedium),
            text = query,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = h5)
    }
}

@Composable
@Preview
private fun Preview() {
    SearchQueryView(Modifier.fillMaxWidth(), "TEST")
}