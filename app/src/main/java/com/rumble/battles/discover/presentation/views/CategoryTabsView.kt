package com.rumble.battles.discover.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
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
fun CategoryTabsView(
    modifier: Modifier,
    initialIndex: Int,
    hasSubcategories: Boolean,
    onTabSelected: (CategoryDisplayType) -> Unit,
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
            CategoryDisplayType.getDisplayTypeList(isPrimary = hasSubcategories)
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