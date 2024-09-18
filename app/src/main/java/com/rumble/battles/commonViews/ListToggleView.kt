package com.rumble.battles.commonViews

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.theme.*
import com.rumble.domain.settings.domain.domainmodel.ListToggleViewStyle

@Composable
fun ListToggleView(
    modifier: Modifier = Modifier,
    selectedViewStyle: ListToggleViewStyle = ListToggleViewStyle.GRID,
    onToggleViewStyle: (listToggleViewStyle: ListToggleViewStyle) -> Unit,
) {
    Row(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = radiusLarge, bottomStart = radiusLarge))
                .border(
                    borderXXSmall,
                    color = getBorderColor(selectedViewStyle, ListToggleViewStyle.GRID),
                    shape = RoundedCornerShape(topStart = radiusLarge, bottomStart = radiusLarge)
                )
                .background(getBackgroundColor(selectedViewStyle, ListToggleViewStyle.GRID))
                .clickable { onToggleViewStyle(ListToggleViewStyle.GRID) }
        ) {
            Icon(
                modifier = Modifier
                    .height(imageMedium)
                    .align(Alignment.Center)
                    .padding(
                        start = paddingXSmall,
                        top = paddingXXXSmall,
                        end = paddingXSmall,
                        bottom = paddingXXXSmall
                    ),
                painter = painterResource(id = R.drawable.ic_grid),
                contentDescription = "",
                tint = getTintColor(selectedViewStyle = selectedViewStyle, currentToggleStyle = ListToggleViewStyle.GRID)
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(topEnd = radiusLarge, bottomEnd = radiusLarge))
                .border(
                    borderXXSmall,
                    color = getBorderColor(selectedViewStyle, ListToggleViewStyle.LIST),
                    shape = RoundedCornerShape(topEnd = radiusLarge, bottomEnd = radiusLarge)
                )
                .background(getBackgroundColor(selectedViewStyle, ListToggleViewStyle.LIST))
                .clickable { onToggleViewStyle(ListToggleViewStyle.LIST) }
        ) {
            Icon(
                modifier = Modifier
                    .height(imageMedium)
                    .align(Alignment.Center)
                    .padding(
                        start = paddingXXXSmall,
                        top = paddingXXXSmall,
                        end = paddingXSmall,
                        bottom = paddingXXXSmall
                    ),
                painter = painterResource(id = R.drawable.ic_list),
                contentDescription = "",
                tint = getTintColor(selectedViewStyle = selectedViewStyle, currentToggleStyle = ListToggleViewStyle.LIST)
            )
        }
    }
}

@Composable
private fun getBorderColor(selectedViewStyle: ListToggleViewStyle, currentToggleStyle: ListToggleViewStyle) =
    if (selectedViewStyle == currentToggleStyle) MaterialTheme.colors.primary else MaterialTheme.colors.secondaryVariant

@Composable
private fun getTintColor(selectedViewStyle: ListToggleViewStyle, currentToggleStyle: ListToggleViewStyle) =
    if (selectedViewStyle == currentToggleStyle) MaterialTheme.colors.onSecondary else MaterialTheme.colors.secondary

@Composable
private fun getBackgroundColor(selectedViewStyle: ListToggleViewStyle, currentToggleStyle: ListToggleViewStyle) =
    if (selectedViewStyle == currentToggleStyle) MaterialTheme.colors.primary else Color.Transparent

@Preview
@Composable
fun PreviewListToggleView() {
    ListToggleView(
        selectedViewStyle = ListToggleViewStyle.LIST
    ) {}
}

@Preview
@Composable
fun PreviewGridToggleView() {
    ListToggleView(
        selectedViewStyle = ListToggleViewStyle.GRID
    ) {}
}