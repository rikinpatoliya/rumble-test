package com.rumble.battles.sort

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.battles.commonViews.RumbleRadioSelectionRow
import com.rumble.domain.sort.SortFollowingType
import com.rumble.theme.RumbleTypography.h3
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXXLarge
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusXMedium

@Composable
fun SortFollowingBottomSheet(
    modifier: Modifier = Modifier,
    sortFollowingType: SortFollowingType,
    onSelected: (SortFollowingType) -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = radiusXMedium, topEnd = radiusXMedium))
            .background(MaterialTheme.colors.background)
    ) {

        Column(
            modifier = Modifier
                .padding(top = paddingXXXSmall, bottom = paddingXXLarge)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.padding(paddingMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.sort_by),
                    color = MaterialTheme.colors.primary,
                    style = h3
                )
                Spacer(modifier = Modifier.weight(1F))
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = stringResource(id = R.string.close),
                    modifier = Modifier
                        .clickable { onClose() },
                    tint = MaterialTheme.colors.primary
                )
            }

            SortFollowingType.values().forEachIndexed { index, followingSort ->
                RumbleRadioSelectionRow(
                    modifier = Modifier
                        .padding(
                            top = paddingMedium,
                            bottom = paddingMedium,
                            start = paddingMedium,
                            end = paddingMedium
                        )
                        .clickable {
                            onSelected(followingSort)
                        },
                    title = stringResource(id = followingSort.nameId),
                    selected = followingSort == sortFollowingType,
                    onSelected = { onSelected(followingSort) }
                )
                if (index != SortFollowingType.values().lastIndex) {
                    Divider(
                        color = MaterialTheme.colors.onSurface
                    )
                }
            }
        }
    }
}