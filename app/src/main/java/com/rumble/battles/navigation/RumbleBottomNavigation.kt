package com.rumble.battles.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.rumble.battles.commonViews.CalculatePaddingForTabletWidth
import com.rumble.battles.commonViews.IsTablet
import com.rumble.theme.bottomBarHeight
import com.rumble.theme.bottomBarTabletHeight
import com.rumble.theme.elevation
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.radiusXXXXMedium
import com.rumble.utils.extension.conditional

@Composable
fun RumbleBottomNavigation(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val configuration = LocalConfiguration.current

    Box(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .padding(
                    start = paddingXXSmall,
                    end = paddingXXSmall,
                    bottom = paddingXXSmall,
                )
        )
    ) {
        Surface(
            modifier = Modifier
                .conditional(!IsTablet()) { fillMaxWidth() }
                .height(if (IsTablet()) bottomBarTabletHeight else bottomBarHeight)
                .padding(
                    horizontal = CalculatePaddingForTabletWidth(
                        configuration.screenWidthDp.dp
                    )
                )
                .align(Alignment.Center),
            shape = RoundedCornerShape(radiusXXXXMedium),
            elevation = elevation
        ) {
            Row(
                Modifier
                    .selectableGroup()
                    .background(MaterialTheme.colors.surface)
                    .align(Alignment.Center),
                horizontalArrangement = Arrangement.SpaceBetween,
                content = content
            )
        }
    }
}