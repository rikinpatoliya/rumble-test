package com.rumble.battles.commonViews

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.rumble.battles.R
import com.rumble.battles.RumbleBasicTopAppBarTag
import com.rumble.theme.RumbleTypography
import com.rumble.theme.borderXXSmall
import com.rumble.theme.logoHeaderHeight
import com.rumble.theme.logoHeaderHeightTablets

@Composable
fun RumbleBasicTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    backButtonColor: Color = MaterialTheme.colors.primary,
    backgroundColor: Color = MaterialTheme.colors.onPrimary,
    isTitleVisible: Boolean = true,
    onBackClick: () -> Unit,
    extraContent: @Composable () -> Unit = {},
) {
    val tablet = IsTablet()
    ConstraintLayout(
        modifier = modifier
            .height(if (tablet) logoHeaderHeightTablets else logoHeaderHeight)
            .background(backgroundColor)
            .testTag(
                RumbleBasicTopAppBarTag
            ),
    ) {
        val (backButton, titleView, extraView, line) = createRefs()
        IconButton(
            modifier = Modifier.constrainAs(backButton) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
            },
            onClick = { onBackClick.invoke() }) {
            Icon(
                Icons.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.back),
                tint = backButtonColor
            )
        }
        AnimatedVisibility(
            visible = isTitleVisible,
            modifier = Modifier.constrainAs(titleView) {
                top.linkTo(backButton.top)
                bottom.linkTo(backButton.bottom)
                centerHorizontallyTo(parent)
            }) {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                style = RumbleTypography.h3
            )
        }
        Box(
            modifier = Modifier
                .constrainAs(extraView) {
                    top.linkTo(backButton.top)
                    bottom.linkTo(backButton.bottom)
                    start.linkTo(titleView.end)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
            contentAlignment = Alignment.CenterEnd
        ) {
            extraContent()
        }

        if (tablet) {
            Divider(
                Modifier
                    .height(borderXXSmall)
                    .fillMaxWidth()
                    .constrainAs(line) { bottom.linkTo(parent.bottom) },
                color = MaterialTheme.colors.secondaryVariant
            )
        }
    }
}