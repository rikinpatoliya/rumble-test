package com.rumble.battles.commonViews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.theme.*
import com.rumble.theme.RumbleTypography.h1
import com.rumble.theme.RumbleTypography.h4

data class ChannelSelectionBottomSheetItem(
    val imageUrl: String,
    val text: String,
    val subText: String = "",
    val selected: Boolean = false,
    val action: () -> Unit,
)

@Composable
fun ChannelSelectionBottomSheet(
    modifier: Modifier = Modifier,
    title: String,
    sheetItems: List<ChannelSelectionBottomSheetItem>,
    onCancel: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .sizeIn(modalMaxWidth)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .systemBarsPadding()
            .imePadding()
            .background(
                color = MaterialTheme.colors.surface,
                shape = RoundedCornerShape(topStart = radiusXMedium, topEnd = radiusXMedium)
            )
            .padding(horizontal = paddingMedium)
            .padding(top = paddingMedium)
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                style = h1
            )

            Icon(
                modifier = Modifier
                    .padding(paddingXXXSmall)
                    .clickable { onCancel() },
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = stringResource(id = R.string.close),
                tint = MaterialTheme.colors.primary
            )
        }

        sheetItems.forEachIndexed { index, it ->
            Row(
                modifier = Modifier
                    .clickable { it.action() }
                    .padding(vertical = paddingMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileImageComponent(
                    modifier = Modifier,
                    profileImageComponentStyle = ProfileImageComponentStyle.CircleImageMediumStyle(),
                    userName = it.text,
                    userPicture = it.imageUrl,
                )
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = paddingXSmall),
                    text = it.text,
                    style = h4
                )
                RadioButton(selected = it.selected)
            }

            if (index < sheetItems.size - 1) {
                Divider(color = MaterialTheme.colors.secondaryVariant)
            }

        }

    }


}