package com.rumble.battles.search.presentation.videosSearch

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.rumble.battles.MatureContentPopupTag
import com.rumble.battles.R
import com.rumble.battles.SearchVideosTag
import com.rumble.battles.commonViews.BottomNavigationBarScreenSpacer
import com.rumble.battles.commonViews.CalculatePaddingForTabletWidth
import com.rumble.battles.commonViews.EmptyView
import com.rumble.battles.commonViews.PageLoadingView
import com.rumble.battles.commonViews.RumbleModalBottomSheetLayout
import com.rumble.battles.commonViews.RumbleProgressIndicator
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.dialogs.DialogActionItem
import com.rumble.battles.commonViews.dialogs.DialogActionType
import com.rumble.battles.commonViews.dialogs.RumbleAlertDialog
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.discover.presentation.views.ErrorView
import com.rumble.battles.feed.presentation.views.VideoCompactView
import com.rumble.battles.search.presentation.views.SearchResultHeader
import com.rumble.battles.sort.SortFilterBottomSheet
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.theme.RumbleTypography.h3
import com.rumble.theme.commentActionButtonWidth
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun VideosSearchScreen(
    handler: VideosSearchHandler,
    contentHandler: ContentHandler,
    onSearch: (String) -> Unit = {},
    onViewVideo: (VideoEntity) -> Unit = {},
    onBack: () -> Unit = {},
    onImpression: (VideoEntity) -> Unit
) {
    val videoEntityList: LazyPagingItems<VideoEntity> =
        handler.videoList.collectAsLazyPagingItems()
    val bottomSheetState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true
        )
    val alertDialogState by handler.alertDialogState
    val coroutineScope = rememberCoroutineScope()
    BackHandler(bottomSheetState.isVisible) {
        coroutineScope.launch { bottomSheetState.hide() }
    }
    val listState by handler.listState

    LaunchedEffect(Unit) {
        handler.eventFlow.collectLatest {
            when (it) {
                is VideosSearchEvent.PlayVideo -> {
                    onViewVideo(it.videoEntity)
                }
            }
        }
    }

    BoxWithConstraints {
        val horizontalContentPadding = CalculatePaddingForTabletWidth(maxWidth = maxWidth)

        RumbleModalBottomSheetLayout(
            sheetState = bottomSheetState,
            sheetContent = {
                SortFilterBottomSheet(
                    modifier = Modifier.padding(horizontal = horizontalContentPadding),
                    bottomSheetState = bottomSheetState,
                    coroutineScope = coroutineScope,
                    selection = handler.selection
                ) {
                    handler.onSelectionMade(it)
                }
            }) {


            Column(
                modifier = Modifier
                    .testTag(SearchVideosTag)
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
                    .systemBarsPadding()
            ) {

                SearchResultHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalContentPadding),
                    query = handler.query,
                    onBack = onBack,
                    onSearch = onSearch
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = paddingMedium + horizontalContentPadding,
                            top = paddingMedium,
                            end = paddingMedium + horizontalContentPadding
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(id = R.string.videos),
                        color = MaterialTheme.colors.secondary,
                        style = h3
                    )

                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                bottomSheetState.show()
                                bottomSheetState.currentValue
                            }
                        }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_filter),
                            contentDescription = stringResource(id = R.string.filter),
                            tint = MaterialTheme.colors.secondary
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingMedium),
                    state = listState,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(paddingLarge),
                    contentPadding = PaddingValues(horizontal = horizontalContentPadding)
                ) {

                    items(
                        count = videoEntityList.itemCount,
                        key = videoEntityList.itemKey(),
                        contentType = videoEntityList.itemContentType(
                        )
                    ) { index ->
                        val item = videoEntityList[index]
                        item?.let {
                            VideoCompactView(
                                videoEntity = item,
                                onViewVideo = { handler.onVideoItemClick(item) },
                                onMoreClick = { contentHandler.onMoreVideoOptionsClicked(item) },
                                onImpression = onImpression
                            )
                        }
                    }

                    item {
                        videoEntityList.apply {
                            when {
                                loadState.refresh is LoadState.NotLoading && itemCount == 0 -> {
                                    EmptyView(
                                        modifier = Modifier
                                            .fillParentMaxSize(),
                                        title = stringResource(id = R.string.no_result),
                                        text = stringResource(id = R.string.try_different_keywords_filters)
                                    )
                                }

                                loadState.refresh is LoadState.Loading -> {
                                    Box(
                                        modifier = Modifier
                                            .fillParentMaxSize()
                                            .padding(horizontal = horizontalContentPadding)
                                    ) {
                                        RumbleProgressIndicator(modifier = Modifier.align(Alignment.Center))
                                    }
                                }

                                loadState.append is LoadState.Loading -> {
                                    PageLoadingView(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight()
                                    )
                                }

                                loadState.append is LoadState.Error -> {
                                    ErrorView(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight(),
                                        backgroundColor = MaterialTheme.colors.onSecondary,
                                        onRetry = ::retry,
                                    )
                                }
                            }
                        }
                    }
                    item { BottomNavigationBarScreenSpacer() }
                }
            }
        }
    }

    if (alertDialogState.show) {
        VideoSearchDialog(
            reason = alertDialogState.alertDialogReason,
            handler = handler
        )
    }
}

@Composable
private fun VideoSearchDialog(reason: AlertDialogReason, handler: VideosSearchHandler) {
    when (reason) {
        is VideosSearchAlertReason.RestrictedContentReason -> {
            RumbleAlertDialog(
                onDismissRequest = { },
                title = stringResource(id = R.string.mature_content),
                text = stringResource(id = R.string.must_be_18),
                testTag = MatureContentPopupTag,
                actionItems = listOf(
                    DialogActionItem(
                        text = stringResource(R.string.cancel),
                        dialogActionType = DialogActionType.Neutral,
                        withSpacer = true,
                        width = commentActionButtonWidth,
                        action = handler::onCancelRestricted
                    ),
                    DialogActionItem(
                        text = stringResource(R.string.start_watching),
                        dialogActionType = DialogActionType.Positive,
                        width = commentActionButtonWidth,
                        action = { handler.onWatchRestricted(reason.videoEntity) }
                    )
                )
            )
        }
    }
}