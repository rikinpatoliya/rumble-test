package com.rumble.battles.commonViews

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.rumble.battles.R
import com.rumble.theme.*
import com.rumble.utils.extension.conditional

@Composable
fun RadioSelectionRow(
    title: String,
    subTitle: String = "",
    description: String = "",
    selected: Boolean,
    expandable: Boolean = false,
    onSelected: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = paddingXSmall)
            .clickable { onSelected() }
    ) {
        val (radioIcon, selectableRow, contentColumn, expandIcon) = createRefs()
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = paddingMedium,
                    top = paddingXSmall,
                    bottom = paddingXSmall
                )
                .constrainAs(selectableRow) {
                    start.linkTo(parent.start)
                    end.linkTo(expandIcon.start)
                    top.linkTo(parent.top)
                    width = Dimension.fillToConstraints
                }
        ) {
            Box(
                modifier = Modifier
                    .padding(top = paddingXXSmall, end = paddingSmall)
                    .constrainAs(radioIcon) {
                        start.linkTo(parent.start)
                        end.linkTo(expandIcon.start)
                        top.linkTo(parent.top)
                        width = Dimension.fillToConstraints
                    },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_radio),
                    contentDescription = stringResource(
                        id = R.string.select
                    ),
                    tint = if (selected) MaterialTheme.colors.secondary else MaterialTheme.colors.secondaryVariant
                )
                if (selected) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_radio_dot),
                        contentDescription = stringResource(
                            id = R.string.select
                        ),
                        tint = rumbleGreen
                    )
                }
            }
            Column(
                modifier = Modifier
                    .conditional(subTitle.isEmpty()) {
                        padding(top = paddingXSmall)
                    }
                    .constrainAs(contentColumn) {
                        start.linkTo(radioIcon.end)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        width = Dimension.fillToConstraints
                    },
            ) {
                Text(
                    text = title,
                    style = RumbleTypography.h4
                )
                if (subTitle.isNotEmpty()) {
                    Text(
                        text = subTitle,
                        style = RumbleTypography.smallBody.copy(color = MaterialTheme.colors.primaryVariant)
                    )
                }
                if (expandable && expanded) {
                    Text(
                        modifier = Modifier
                            .padding(top = paddingXXXSmall),
                        text = description,
                        style = RumbleTypography.h6Light
                    )
                }
            }
        }
        if (expandable) {
            IconButton(
                onClick = { expanded = !expanded },
                modifier = Modifier
                    .constrainAs(expandIcon) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                    }
            ) {
                Icon(
                    painter = painterResource(id = if (expanded) R.drawable.ic_chevron_up else R.drawable.ic_chevron_down),
                    contentDescription = stringResource(
                        id = R.string.description
                    ),
                    tint = MaterialTheme.colors.primaryVariant
                )
            }
        }
    }
}