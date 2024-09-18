package com.rumble.battles.commonViews

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import com.rumble.battles.R
import com.rumble.battles.common.parseTextWithUrls
import com.rumble.domain.common.domain.usecase.AnnotatedStringWithActionsList
import com.rumble.theme.RumbleTypography
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXMedium
import com.rumble.theme.paddingXSmall
import com.rumble.utils.extension.clickableNoRipple
import com.rumble.utils.extension.conditional

const val DEFAULT_MINIMUM_TEXT_LINE = 3

@Composable
fun ExpandableText(
    modifier: Modifier = Modifier,
    text: String,
    collapsedMaxLine: Int = DEFAULT_MINIMUM_TEXT_LINE,
    onUriClick: (String) -> Unit = {},
    onAnnotatedTextClicked: (AnnotatedStringWithActionsList, Int) -> Unit = { _, _ -> }
) {
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val isExpandable by remember { derivedStateOf { textLayoutResult?.didOverflowHeight ?: false } }
    var isExpanded by remember { mutableStateOf(false) }
    val showButtonAside by remember { derivedStateOf { isExpandable && isExpanded.not() } }

    val annotatedTextWithActions: AnnotatedStringWithActionsList =
        parseTextWithUrls(
            text = text,
            color = MaterialTheme.colors.secondary,
            onClick = onUriClick
        )

    Column(
        modifier = modifier.animateContentSize(),
        horizontalAlignment = Alignment.Start
    ) {
        Box {
            ClickableText(
                modifier = Modifier.fillMaxWidth(),
                text = annotatedTextWithActions.annotatedString,
                maxLines = if (isExpanded) Int.MAX_VALUE else collapsedMaxLine,
                style = RumbleTypography.h5Medium,
                onClick = { offset ->
                    onAnnotatedTextClicked(annotatedTextWithActions, offset)
                },
                onTextLayout = { textLayoutResult = it }
            )

            if (showButtonAside) {
                ShowMoreLessButton(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    viewCollapsed = true,
                    onClick = { isExpanded = isExpanded.not() }
                )
            }
        }

        if (isExpanded) {
            ShowMoreLessButton(
                modifier = Modifier.padding(top = paddingSmall),
                viewCollapsed = false,
                onClick = { isExpanded = isExpanded.not() }
            )
        }
    }

}

@Composable
private fun ShowMoreLessButton(
    modifier: Modifier = Modifier,
    viewCollapsed: Boolean = true,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .clickableNoRipple { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (viewCollapsed) {
            Box(
                modifier = Modifier
                    .width(paddingXMedium)
                    .fillMaxHeight()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colors.surface.copy(alpha = 0f),
                                MaterialTheme.colors.surface
                            )
                        )
                    )
            )
        }
        Text(
            modifier = Modifier
                .background(MaterialTheme.colors.surface)
                .conditional(viewCollapsed) {
                    padding(start = paddingXSmall)
                },
            text = if (viewCollapsed) stringResource(id = R.string.show_more) else stringResource(id = R.string.show_less),
            color = MaterialTheme.colors.primary,
            style = RumbleTypography.h6
        )
    }
}
