package com.rumble.battles.discover.presentation.discoverscreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rumble.analytics.CardSize
import com.rumble.battles.DiscoverMainContentColumn
import com.rumble.battles.DiscoverTag
import com.rumble.battles.MatureContentPopupTag
import com.rumble.battles.R
import com.rumble.battles.SwipeRefreshTag
import com.rumble.battles.commonViews.BottomNavigationBarScreenSpacer
import com.rumble.battles.commonViews.CalculatePaddingForTabletWidth
import com.rumble.battles.commonViews.RumbleLogoSearchHeaderView
import com.rumble.battles.commonViews.RumbleSwipeRefreshIndicator
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.dialogs.DialogActionItem
import com.rumble.battles.commonViews.dialogs.DialogActionType
import com.rumble.battles.commonViews.dialogs.RumbleAlertDialog
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.content.presentation.ContentScreenVmEvent
import com.rumble.battles.discover.presentation.views.DontMissItView
import com.rumble.battles.discover.presentation.views.EditorPicksView
import com.rumble.battles.discover.presentation.views.LiveCategoriesView
import com.rumble.battles.discover.presentation.views.LiveNowList
import com.rumble.battles.discover.presentation.views.PopularVideosView
import com.rumble.battles.discover.presentation.views.TopChannelsView
import com.rumble.battles.landing.RumbleActivityHandler
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.videolist.domain.model.VideoList
import com.rumble.theme.commentActionButtonWidth
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXLarge
import kotlinx.coroutines.flow.collectLatest

private enum class ContentSection {
    HurryDontMiss,
    Categories,
    LiveNow,
    EditorPicks,
    TopChannels,
    Popular;
}

@Composable
fun DiscoverScreen(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    activityHandler: RumbleActivityHandler,
    discoverHandler: DiscoverHandler,
    contentHandler: ContentHandler,
    listState: LazyListState,
    onSearch: () -> Unit = {},
    onChannelClick: (id: String) -> Unit = {},
    onVideoClick: (video: VideoEntity) -> Unit = {},
    onViewCategory: (VideoList) -> Unit = {},
    onViewTopChannels: () -> Unit = {},
    onBrowseCategory: (String) -> Unit = {},
    onBrowseAllCategories: () -> Unit = {},
    onViewNotifications: () -> Unit,
) {
    val state by discoverHandler.state.collectAsStateWithLifecycle()
    val userUIState by contentHandler.userUIState.collectAsStateWithLifecycle()
    val activityHandlerState by activityHandler.activityHandlerState.collectAsStateWithLifecycle()
    val alertDialogState by discoverHandler.alertDialogState
    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_PAUSE) {
            discoverHandler.onPauseCurrentPlayer()
        } else if (event == Lifecycle.Event.ON_RESUME) {
            discoverHandler.onViewResumed()
            discoverHandler.onVideoPlayerImpression()
        }
    }

    BackHandler {
        contentHandler.onNavigateHome()
    }

    LaunchedEffect(Unit) {
        contentHandler.eventFlow.collectLatest {
            if (it is ContentScreenVmEvent.ScrollToTop) {
                listState.animateScrollToItem(0)
            }
        }
    }

    LaunchedEffect(Unit) {
        discoverHandler.eventFlow.collectLatest {
            when (it) {
                is DiscoverEvent.PlayVideo -> {
                    onVideoClick(it.videoEntity)
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            discoverHandler.onPauseCurrentPlayer()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .testTag(DiscoverTag)
            .background(MaterialTheme.colors.background)
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        val (header, list) = createRefs()

        RumbleLogoSearchHeaderView(
            modifier = Modifier.constrainAs(header) { top.linkTo(parent.top) },
            hasUnreadNotifications = activityHandlerState.hasUnreadNotifications,
            userLoggedIn = userUIState.isLoggedIn,
            onSearch = onSearch,
            onNotifications = {
                activityHandler.clearNotifications()
                onViewNotifications()
            },
        )

        SwipeRefresh(
            modifier = Modifier
                .testTag(SwipeRefreshTag)
                .constrainAs(list) {
                    top.linkTo(header.bottom)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints
                },
            state = rememberSwipeRefreshState(
                state.featuredChannelsLoading ||
                    state.popularVideosLoading ||
                    state.doNotMissItLoading ||
                    state.editorPicksLoading ||
                    state.categoryListLoading ||
                    state.liveNowLoading,
            ),
            onRefresh = {
                discoverHandler.refresh()
                activityHandler.loadNotificationState()
            },
            indicator = { state, trigger -> RumbleSwipeRefreshIndicator(state, trigger) }
        ) {

            BoxWithConstraints {

                LazyColumn(
                    modifier = Modifier
                        .semantics {
                            testTag = DiscoverMainContentColumn
                        },
                    state = listState,
                    contentPadding =
                    PaddingValues(
                        top = paddingMedium,
                        start = CalculatePaddingForTabletWidth(maxWidth),
                        end = CalculatePaddingForTabletWidth(maxWidth),
                    )
                ) {
                    items(ContentSection.values()) {
                        when (it) {

                            ContentSection.HurryDontMiss -> DontMissItView(
                                modifier = Modifier
                                    .padding(horizontal = paddingMedium)
                                    .padding(bottom = paddingLarge),
                                contentHandler = contentHandler,
                                doNotMissItVideo = state.doNotMissItVideo,
                                loading = state.doNotMissItLoading,
                                error = state.doNotMissItError,
                                soundOn = state.soundOn,
                                rumblePlayer = state.dontMissPlayer,
                                onSoundClick = discoverHandler::onSoundClick,
                                onChannelClick = onChannelClick,
                                onVideoClick = {
                                    state.doNotMissItVideo?.let { videoEntity ->
                                        discoverHandler.onVideoItemClick(videoEntity)
                                    }
                                },
                                onLike = discoverHandler::like,
                                onDislike = discoverHandler::dislike,
                                onRefresh = discoverHandler::refreshHurryDoNotMissItVideo,
                                onImpression = { video ->
                                    discoverHandler.onVideoCardImpression(video, CardSize.REGULAR)
                                    discoverHandler.onDontMissViewVisible()
                                },
                                onInvisible = discoverHandler::onDontMissViewInvisible
                            )

                            ContentSection.Categories -> {
                                LiveCategoriesView(
                                    modifier = Modifier.padding(bottom = paddingXLarge),
                                    title = stringResource(id = R.string.live_categories).uppercase(),
                                    titleHorizontalPadding = paddingSmall,
                                    titleBottomPadding = paddingSmall,
                                    isLoading = state.categoryListLoading,
                                    error = state.categoryListError,
                                    viewAll = true,
                                    categoryList = state.categoryList,
                                    onViewCategory = { categoryEntity, _ ->
                                        discoverHandler.onCategoryClick(categoryEntity)
                                        onBrowseCategory(categoryEntity.path)
                                    },
                                    onRefresh = discoverHandler::refreshCategoryList,
                                    onViewAll = {
                                        discoverHandler.onViewAllCategoriesClick()
                                        onBrowseAllCategories()
                                    }
                                )
                            }

                            ContentSection.LiveNow -> LiveNowList(
                                modifier = Modifier.padding(
                                    start = paddingMedium,
                                    end = paddingMedium,
                                    bottom = paddingXLarge
                                ),
                                liveNowVideos = state.liveNowVideos,
                                loading = state.liveNowLoading,
                                error = state.liveNowError,
                                onVideoClick = discoverHandler::onVideoItemClick,
                                onChannelClick = onChannelClick,
                                onRefresh = discoverHandler::refreshLiveVideos,
                                onViewAll = { onViewCategory(VideoList.Live) }
                            )

                            ContentSection.EditorPicks -> EditorPicksView(
                                modifier = Modifier.padding(
                                    start = paddingMedium,
                                    end = paddingMedium,
                                    bottom = paddingXLarge
                                ),
                                editorPicks = state.editorPicks,
                                loading = state.editorPicksLoading,
                                error = state.editorPicksError,
                                onVideoClick = discoverHandler::onVideoItemClick,
                                onMoreClick = { video ->
                                    contentHandler.onMoreVideoOptionsClicked(video)
                                },
                                onRefresh = discoverHandler::refreshEditorPickVideos,
                                onViewCategory = { onViewCategory(VideoList.EditorPicks) },
                                onImpression = { video ->
                                    discoverHandler.onVideoCardImpression(video, CardSize.COMPACT)
                                }
                            )

                            ContentSection.TopChannels -> TopChannelsView(
                                modifier = Modifier.padding(
                                    start = paddingMedium,
                                    end = paddingMedium,
                                    bottom = paddingXLarge
                                ),
                                featuredChannels = state.featuredChannels,
                                loading = state.featuredChannelsLoading,
                                error = state.featuredChannelsError,
                                onChannelClick = onChannelClick,
                                onRefresh = discoverHandler::refreshPopularVideos,
                                onViewAll = onViewTopChannels
                            )

                            ContentSection.Popular -> PopularVideosView(
                                modifier = Modifier.padding(
                                    start = paddingMedium,
                                    end = paddingMedium,
                                    bottom = paddingXLarge
                                ),
                                popularVideos = state.popularVideos,
                                loading = state.popularVideosLoading,
                                error = state.popularVideosError,
                                onMoreClick = { video ->
                                    contentHandler.onMoreVideoOptionsClicked(video)
                                },
                                onVideoClick = discoverHandler::onVideoItemClick,
                                onRefresh = discoverHandler::refreshPopularVideos,
                                onViewAll = { onViewCategory(VideoList.Popular) },
                                onImpression = { video ->
                                    discoverHandler.onVideoCardImpression(video, CardSize.COMPACT)
                                }
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

    if (alertDialogState.show) {
        DiscoverDialog(
            reason = alertDialogState.alertDialogReason,
            handler = discoverHandler
        )
    }
}

@Composable
private fun DiscoverDialog(reason: AlertDialogReason, handler: DiscoverHandler) {
    when (reason) {
        is DiscoverAlertReason.RestrictedContentReason -> {
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
                        action = handler::onWatchRestricted
                    )
                )
            )
        }
    }
}