package com.rumble.battles.commonViews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rumble.battles.R
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTypography.body1
import com.rumble.theme.RumbleTypography.h3
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.bottomSheepOptionMinHeight
import com.rumble.theme.imageSmall
import com.rumble.theme.modalMaxWidth
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusXMedium

data class BottomSheetItem(
    val imageResource: Int? = null,
    val imageUrl: String? = null,
    val text: String,
    val subText: String? = null,
    val action: () -> Unit,
)

@Composable
fun RumbleBottomSheet(
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    sheetItems: List<BottomSheetItem>,
    onCancel: () -> Unit = {}
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .sizeIn(modalMaxWidth)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .imePadding()
            .padding(paddingSmall)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(radiusXMedium))
                .background(RumbleCustomTheme.colors.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (title != null || subtitle != null) {
                    Spacer(
                        Modifier
                            .height(paddingMedium)
                    )
                }
                title?.let {
                    Text(
                        text = it,
                        modifier = Modifier.padding(
                            start = paddingMedium,
                            end = paddingMedium
                        ),
                        style = h3,
                        color = RumbleCustomTheme.colors.primary
                    )
                }

                subtitle?.let {
                    Text(
                        modifier = Modifier.padding(
                            top = paddingXXXSmall,
                            start = paddingMedium,
                            end = paddingMedium
                        ),
                        text = it,
                        style = body1,
                        color = RumbleCustomTheme.colors.secondary
                    )
                }

                sheetItems.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .sizeIn(minHeight = bottomSheepOptionMinHeight)
                            .clickable { item.action() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item.imageResource?.let {
                            Icon(
                                modifier = Modifier
                                    .padding(start = paddingMedium)
                                    .size(imageSmall),
                                painter = painterResource(id = item.imageResource),
                                contentDescription = item.text,
                                tint = RumbleCustomTheme.colors.primary
                            )
                        }
                        item.imageUrl?.let {
                            ProfileImageComponent(
                                modifier = Modifier
                                    .padding(
                                        start = paddingSmall,
                                        bottom = paddingXSmall,
                                        end = paddingXSmall
                                    ),
                                profileImageComponentStyle = ProfileImageComponentStyle.CircleImageMediumStyle(),
                                userName = item.text,
                                userPicture = item.imageUrl
                            )
                        }
                        Column {
                            Text(
                                modifier = Modifier.padding(horizontal = paddingMedium),
                                text = item.text,
                                style = h4,
                                color = RumbleCustomTheme.colors.primary
                            )
                            item.subText?.let {
                                Text(
                                    modifier = Modifier.padding(horizontal = paddingMedium),
                                    text = it,
                                    style = h6,
                                    color = RumbleCustomTheme.colors.secondary
                                )
                            }
                        }

                    }

                    if (sheetItems.last() != item) {
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = paddingMedium, end = paddingMedium)
                                .width(1.dp),
                            color = RumbleCustomTheme.colors.backgroundHighlight
                        )
                    }
                }
            }
        }

        MainActionButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = paddingMedium),
            text = stringResource(id = R.string.cancel),
            backgroundColor = RumbleCustomTheme.colors.surface,
            textColor = RumbleCustomTheme.colors.primary,
            onClick = onCancel,
            textStyle = h4
        )
    }
}
