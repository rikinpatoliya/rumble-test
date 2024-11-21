package com.rumble.battles.commonViews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.rumble.theme.RumbleTypography
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.modalMaxWidth
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXSmall
import com.rumble.theme.radiusXMedium

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
        BottomSheetHeader(
            title = title,
            onClose = onCancel
        )

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
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = paddingXSmall)
                ) {
                    Text(
                        text = it.text,
                        style = h4,
                        color = MaterialTheme.colors.primary,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )
                    if (it.subText.isNotEmpty()) {
                        Text(
                            text = it.subText,
                            style = RumbleTypography.h6Light,
                            color = MaterialTheme.colors.secondary,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                RadioButton(selected = it.selected)
            }

            if (index < sheetItems.size - 1) {
                Divider(color = MaterialTheme.colors.secondaryVariant)
            }

        }

    }


}