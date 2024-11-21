package com.rumble.battles.commonViews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.rumble.battles.R
import com.rumble.theme.RumbleTypography
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusMedium

@Composable
fun SelectChannelRowView(
    title: String,
    description: String,
    onClick: () -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = paddingMedium,
                end = paddingMedium,
                top = paddingXXXSmall
            )
            .clip(RoundedCornerShape(radiusMedium))
            .background(MaterialTheme.colors.onSurface)
            .clickable { onClick() },
    ) {
        val (text, icon) = createRefs()
        Column(
            modifier = Modifier
                .padding(start = paddingMedium, top = paddingMedium, bottom = paddingMedium)
                .constrainAs(text) {
                    start.linkTo(parent.start)
                    end.linkTo(icon.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                }
        ) {
            Text(
                text = title,
                color = MaterialTheme.colors.primary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = RumbleTypography.h4,
                textAlign = TextAlign.Start
            )
            Text(
                text = description,
                color = MaterialTheme.colors.primaryVariant,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = RumbleTypography.smallBody,
                textAlign = TextAlign.Start
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_down),
            contentDescription = stringResource(id = R.string.select_a_channel),
            modifier = Modifier
                .padding(end = paddingMedium)
                .constrainAs(icon) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
            tint = MaterialTheme.colors.primaryVariant
        )
    }
}
