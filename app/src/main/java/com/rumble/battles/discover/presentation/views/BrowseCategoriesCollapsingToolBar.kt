package com.rumble.battles.discover.presentation.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.battles.commonViews.RumbleBasicTopAppBar
import com.rumble.battles.commonViews.RumbleTabsView
import com.rumble.domain.discover.domain.domainmodel.CategoryDisplayType
import com.rumble.domain.discover.domain.domainmodel.MainCategory
import com.rumble.theme.RumbleTheme

@Composable
fun BrowseCategoriesCollapsingToolBar(
    modifier: Modifier = Modifier,
    isCollapsed: Boolean = false,
    categoryDisplayType: CategoryDisplayType,
    onSearch: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onTabSelected: (CategoryDisplayType) -> Unit = {},
    onCategoryClick: (MainCategory) -> Unit = {}
) {
    Column(modifier = modifier) {
        RumbleBasicTopAppBar(
            title = stringResource(id = R.string.browse),
            modifier = Modifier.fillMaxWidth(),
            onBackClick = onBackClick,
            extraContent = {
                IconButton(
                    onClick = onSearch
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = stringResource(id = R.string.search_rumble),
                    )
                }
            }
        )

        AnimatedVisibility(visible = isCollapsed.not()) {
            Column {
                MainCategoryListView(
                    modifier = Modifier.fillMaxWidth(),
                    onCategoryClick = onCategoryClick
                )

                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colors.secondaryVariant
                )
            }
        }

        RumbleTabsView(
            modifier = Modifier.fillMaxWidth(),
            tabsList = CategoryDisplayType.getMainCategoryTypeList(),
            onTabSelected = onTabSelected,
            initialIndex = categoryDisplayType.mainIndex
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    RumbleTheme {
        BrowseCategoriesCollapsingToolBar(categoryDisplayType = CategoryDisplayType.CATEGORIES)
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewCollapsed() {
    RumbleTheme {
        BrowseCategoriesCollapsingToolBar(
            isCollapsed = true,
            categoryDisplayType = CategoryDisplayType.CATEGORIES
        )
    }
}
