package com.rumble.battles.discover.presentation.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.domain.discover.domain.domainmodel.MainCategory
import com.rumble.theme.RumbleTheme
import com.rumble.theme.paddingSmall
import com.rumble.utils.RumbleConstants

@Composable
fun MainCategoryListView(
    modifier: Modifier = Modifier,
    onCategoryClick: (MainCategory) -> Unit
) {
    val gridState: LazyGridState = rememberLazyGridState()

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(RumbleConstants.MAIN_CATEGORY_ITEMS_IN_ROW_QUANTITY),
        contentPadding = PaddingValues(paddingSmall),
        verticalArrangement = Arrangement.spacedBy(paddingSmall),
        horizontalArrangement = Arrangement.spacedBy(paddingSmall),
        state = gridState
    ) {
        items(MainCategory.values()) { mainCategory ->
            MainCategoryView(
                mainCategory = mainCategory,
                onClick = onCategoryClick
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    RumbleTheme {
        MainCategoryListView {}
    }
}