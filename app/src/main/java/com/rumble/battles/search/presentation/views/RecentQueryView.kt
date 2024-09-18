package com.rumble.battles.search.presentation.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.battles.common.buildDelimiterHighlightedAnnotatedString
import com.rumble.domain.search.domain.domainModel.RecentQuery
import com.rumble.theme.RumbleTypography.h5
import com.rumble.theme.enforcedCloud
import com.rumble.theme.imageXSmall14
import com.rumble.theme.imageXXSmall
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXSmall

@Composable
fun RecentQueryView(
    modifier: Modifier = Modifier,
    recentQuery: RecentQuery,
    query: String,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    val annotatedText = buildDelimiterHighlightedAnnotatedString(
        input = recentQuery.query,
        delimiter = query,
        regularStyle = SpanStyle(
            fontFamily = h5.fontFamily,
            fontWeight = h5.fontWeight,
            fontSize = h5.fontSize,
        ),
        highlightedStyle = SpanStyle(
            fontFamily = h5.fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = h5.fontSize,
        ),
    )

    Box(modifier = modifier
        .clickable {
            onClick()
        }
    ) {
        Row(
            modifier = modifier
                .padding(start = paddingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_time),
                contentDescription = "",
                modifier = Modifier
                    .padding(start = paddingXSmall, end = paddingMedium)
                    .size(imageXXSmall),
                tint = MaterialTheme.colors.primary
            )
            Text(
                modifier = Modifier
                    .weight(1f),
                text = annotatedText,
                color = MaterialTheme.colors.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            IconButton(
                onClick = onDelete
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "",
                    modifier = Modifier
                        .size(imageXSmall14),
                    tint = enforcedCloud
                )
            }
        }
    }
}

@Composable
@Preview
private fun Preview() {
    RecentQueryView(
        modifier = Modifier.fillMaxWidth(),
        recentQuery = RecentQuery(query = "Test query"),
        query = "Test",
        {},
        {}
    )
}