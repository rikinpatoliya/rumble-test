package com.rumble.battles.discover.presentation.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.battles.commonViews.RumbleBasicTopAppBar
import com.rumble.domain.discover.domain.domainmodel.CategoryDisplayType
import com.rumble.domain.discover.domain.domainmodel.CategoryEntity
import com.rumble.theme.paddingLarge

@Composable
fun CategoryCollapsingToolBar(
    modifier: Modifier = Modifier,
    isCollapsed: Boolean,
    category: CategoryEntity?,
    categoryDisplayType: CategoryDisplayType,
    hasSubcategories: Boolean,
    isLoading: Boolean,
    onSearch: () -> Unit,
    onBackClick: () -> Unit,
    onTabSelected: (CategoryDisplayType) -> Unit
) {
    Column(modifier = modifier) {
        RumbleBasicTopAppBar(
            title = category?.title ?: "",
            modifier = Modifier.fillMaxWidth(),
            isTitleVisible = isCollapsed,
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
            CategoryHeaderView(
                modifier = Modifier
                    .padding(top = paddingLarge)
                    .fillMaxWidth(),
                category = category,
                isLoading = isLoading
            )
        }

        CategoryTabsView(
            modifier = Modifier.fillMaxWidth(),
            hasSubcategories = hasSubcategories,
            onTabSelected = onTabSelected,
            initialIndex = categoryDisplayType.index
        )
    }
}

