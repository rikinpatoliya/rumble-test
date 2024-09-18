package com.rumble.battles.discover.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.rumble.domain.discover.domain.domainmodel.CategoryDisplayType
import com.rumble.theme.RumbleTypography
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingNone
import com.rumble.theme.rumbleGreen

@Composable
fun MainCategoryTabsView(
    modifier: Modifier,
    initialIndex: Int,
    onTabSelected: (CategoryDisplayType) -> Unit
) {
    var tabIndex by remember { mutableIntStateOf(initialIndex) }
    Box(modifier = modifier) {
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            color = MaterialTheme.colors.secondaryVariant
        )

        ScrollableTabRow(
            selectedTabIndex = tabIndex,
            backgroundColor = Color.Transparent,
            contentColor = rumbleGreen,
            edgePadding = paddingNone,
            divider = {},
            indicator = { tabPositions ->
                Box(
                    Modifier
                        .tabIndicatorOffset(tabPositions[tabIndex])
                        .height(TabRowDefaults.IndicatorHeight)
                        .padding(start = paddingMedium, end = paddingMedium)
                        .background(color = rumbleGreen)
                )
            }
        ) {
            CategoryDisplayType.getMainCategoryTypeList()
                .mapIndexed { index, categorySubtype ->
                    Tab(
                        text = {
                            Text(
                                text = stringResource(categorySubtype.label),
                                style = RumbleTypography.h4,
                                color = MaterialTheme.colors.primary
                            )
                        },
                        selected = tabIndex == index,
                        onClick = {
                            tabIndex = index
                            onTabSelected(categorySubtype)
                        }
                    )
                }
        }
    }
}