package com.rumble.battles.search.presentation.searchScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumble.battles.R
import com.rumble.battles.SearchQueryCancelButtonTag
import com.rumble.battles.SearchQueryClearAllTag
import com.rumble.battles.SearchQueryTag
import com.rumble.battles.commonViews.BottomNavigationBarScreenSpacer
import com.rumble.battles.commonViews.CalculatePaddingForTabletWidth
import com.rumble.battles.commonViews.RumbleTextActionButton
import com.rumble.battles.search.presentation.views.AutoCompleteSearchCategoryView
import com.rumble.battles.search.presentation.views.AutoCompleteSearchChannelView
import com.rumble.battles.search.presentation.views.RecentQueryView
import com.rumble.battles.search.presentation.views.SearchView
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXMedium
import com.rumble.theme.wokeGreen
import com.rumble.utils.extension.rumbleUitTestTag

@Composable
fun SearchScreen(
    searchHandler: SearchHandler,
    onSearch: (String, String, String) -> Unit,
    onViewChannel: (String) -> Unit,
    onBrowseCategory: (String) -> Unit,
    onCancel: () -> Unit = {}
) {

    val state by searchHandler.state.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState by searchHandler.listState

    BoxWithConstraints {

        Column(
            modifier = Modifier
                .testTag(SearchQueryTag)
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .padding(horizontal = CalculatePaddingForTabletWidth(maxWidth = maxWidth))
                .systemBarsPadding()
        ) {
            Row(
                modifier = Modifier.padding(top = paddingMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchView(
                    modifier = Modifier
                        .padding(start = paddingMedium)
                        .weight(1f),
                    initialQuery = searchHandler.initialQuery,
                    onSearch = {
                        searchHandler.saveQuery(it)
                        onSearch(it, searchHandler.navDest, searchHandler.parentScreen)
                    },
                    onQueryChanged = {
                        searchHandler.onQueryChanged(it)
                    }
                )

                RumbleTextActionButton(
                    modifier = Modifier
                        .rumbleUitTestTag(SearchQueryCancelButtonTag)
                        .padding(end = paddingXSmall),
                    text = stringResource(id = R.string.cancel)
                ) {
                    keyboardController?.hide()
                    onCancel()
                }
            }


            if (state.recentQueryList.isNotEmpty() && state.query.isEmpty()) {
                Row(
                    modifier = Modifier.padding(
                        start = paddingMedium,
                        top = paddingLarge,
                        end = paddingXXMedium
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.recent),
                        style = h4
                    )
                    Spacer(modifier = Modifier.weight(1F))
                    Text(
                        modifier = Modifier
                            .semantics { contentDescription = SearchQueryClearAllTag }
                            .clickable { searchHandler.onDeleteAllRecentQueries() },
                        text = stringResource(id = R.string.clear_all),
                        color = wokeGreen,
                        style = h6
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .imePadding()
                    .fillMaxWidth()
                    .padding(top = paddingXSmall),
                state = listState,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(state.recentQueryList) { index, recentQuery ->
                    RecentQueryView(
                        modifier = Modifier
                            .fillMaxWidth(),
                        recentQuery = recentQuery,
                        query = state.query,
                        index = index,
                        onClick = {
                            searchHandler.updateQuery(recentQuery)
                            onSearch(
                                recentQuery.query,
                                searchHandler.navDest,
                                searchHandler.parentScreen
                            )
                        },
                        onDelete = { searchHandler.onDeleteRecentQuery(recentQuery) }
                    )
                }
                if (state.query.isNotEmpty()) {
                    itemsIndexed(state.autoCompleteChannelsList) { index, autoCompleteChannel ->
                        AutoCompleteSearchChannelView(
                            channelDetailsEntity = autoCompleteChannel,
                            query = state.query,
                            index = index,
                            onViewChannel = onViewChannel
                        )
                    }
                    items(state.autoCompleteCategoriesList) { autoCompleteCategory ->
                        AutoCompleteSearchCategoryView(
                            categoryEntity = autoCompleteCategory,
                            query = state.query,
                            onBrowseCategory = onBrowseCategory
                        )
                    }
                }
                item {
                    BottomNavigationBarScreenSpacer()
                }
            }
        }
    }
}
