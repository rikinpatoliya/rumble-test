package com.rumble.ui3.library

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import androidx.tv.foundation.PivotOffsets
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvGridItemSpan
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.itemsIndexed
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rumble.R
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.library.domain.model.LibraryCollection
import com.rumble.theme.RumbleTvTypography.h2Tv
import com.rumble.theme.RumbleTvTypography.h3Tv
import com.rumble.theme.RumbleTvTypography.labelBoldTv
import com.rumble.theme.RumbleTvTypography.labelRegularTv
import com.rumble.theme.browseGridColumnStartPadding
import com.rumble.theme.browseLeftNavMenuItemHeight
import com.rumble.theme.browseLeftNavMenuWidth
import com.rumble.theme.enforcedBone
import com.rumble.theme.enforcedCloud
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedGray950
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXLarge
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXMedium
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusXMedium
import com.rumble.theme.refreshButtonIconSize
import com.rumble.theme.refreshButtonSize
import com.rumble.theme.rumbleGreen
import com.rumble.theme.tvButtonHeightSmall
import com.rumble.theme.tvLazyVerticalGridBottomPadding
import com.rumble.theme.tvLibrarySubHeaderMaxWidth
import com.rumble.theme.tvLibraryTitleTextMaxWidth
import com.rumble.theme.videoCardWidth
import com.rumble.ui3.common.views.ErrorView
import com.rumble.ui3.common.views.PageLoadingView
import com.rumble.ui3.common.views.RumbleProgressIndicator
import com.rumble.ui3.library.FocusedElement.LibraryList
import com.rumble.ui3.library.FocusedElement.None
import com.rumble.ui3.library.FocusedElement.PlayAllButton
import com.rumble.ui3.library.FocusedElement.RefreshButton
import com.rumble.ui3.library.FocusedElement.ShuffleAllButton
import com.rumble.ui3.library.FocusedElement.VideoGrid
import com.rumble.utils.RumbleConstants.TV_LIBRARY_SCREEN_GRID_WIDTH
import java.util.UUID
import kotlin.math.max

private enum class FocusedElement {
    RefreshButton,
    PlayAllButton,
    ShuffleAllButton,
    VideoGrid,
    LibraryList,
    None
}

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LibraryScreen(
    viewModel: LibraryHandler,
    focusRequester: FocusRequester,
    onNavigateToVideoPlayer: (VideoEntity) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToPlayAll: (title: String, videoList: List<Feed>, shuffle: Boolean) -> Unit,
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val videoListItems: LazyPagingItems<Feed> = state.videoList.collectAsLazyPagingItems()
    val playLists: LazyPagingItems<PlayListEntity> = state.playLists.collectAsLazyPagingItems()

    val libraryItems = listOf(
        LibraryCollection.WatchHistory,
        LibraryCollection.WatchLater,
        LibraryCollection.Purchases,
        LibraryCollection.Liked
    )

    val refreshFocusRequester = remember { FocusRequester() }

    val videoGridFocusRequester = remember { FocusRequester() }
    val videoGridFocusRequesters = remember { mutableMapOf<UUID, FocusRequester>() }

    var focusedElement by remember { mutableStateOf(None) }
    var focusedIndex by remember { mutableIntStateOf(-1) }
    var isMenuVisible by remember { mutableStateOf(true) }
    val previousIsMenuVisible = rememberPreviousState(isMenuVisible)

    val libraryFocusRequesters = List(size = libraryItems.size) { FocusRequester() }
    val playlistFocusRequesters =
        List(size = max(0, playLists.itemCount)) { FocusRequester() }

    val lazyColumnState = rememberTvLazyListState()

    if (state.fetchingLoggedInState) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            RumbleProgressIndicator()
        }
    } else if (state.loggedIn.not()) {
        NotLoggedIn(focusRequester) { onNavigateToLogin() }
    } else {
        Row(
            modifier = Modifier
                .fillMaxSize()
        ) {

            AnimatedVisibility(visible = isMenuVisible) {
                Column(
                    modifier = Modifier
                        .width(browseLeftNavMenuWidth)
                        .fillMaxHeight()
                        .background(enforcedGray950.copy(alpha = .6f)),
                ) {
                    Text(
                        modifier = Modifier.padding(
                            start = paddingMedium,
                            end = paddingMedium,
                            top = paddingXXMedium,
                            bottom = paddingSmall
                        ),
                        text = stringResource(id = R.string.library_screen_title),
                        style = h3Tv,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    TvLazyColumn(
                        modifier = Modifier
                            .focusProperties {
                                enter = {
                                    when (val selectedList = state.selectedList) {
                                        is ListSelectionType.Library -> {
                                            val index = libraryItems.indexOf(selectedList.libraryCollection)
                                            libraryFocusRequesters.getOrNull(index)
                                        }

                                        is ListSelectionType.PlayList -> {
                                            val index =
                                                playLists.itemSnapshotList.indexOf(selectedList.playListEntity)
                                            playlistFocusRequesters.getOrNull(index)
                                        }
                                    } ?: FocusRequester.Default
                                }
                            }
                            .onFocusChanged {
                                if (it.hasFocus || it.isFocused) {
                                    focusedElement = LibraryList
                                }
                            }
                            .focusRequester(focusRequester)
                            .fillMaxSize(),
                        state = lazyColumnState
                    ) {

                        itemsIndexed(libraryItems) { index, item ->
                            PlaylistView(
                                modifier = Modifier.padding(horizontal = paddingMedium),
                                onFocused = {
                                    viewModel.onFocusedPlayList(ListSelectionType.Library(item))
                                },
                                onClick = {
                                    isMenuVisible = false
                                    if (videoListItems.itemCount == 0) {
                                        refreshFocusRequester.requestFocus()
                                    } else {
                                        videoGridFocusRequester.requestFocus()
                                    }
                                },
                                focusRequester = libraryFocusRequesters[index],
                                title = getLibraryCollectionTitle(libraryCollection = item),
                                showImage = false,
                            )
                        }

                        item {
                            Text(
                                modifier = Modifier.padding(
                                    start = paddingMedium,
                                    end = paddingMedium,
                                    top = paddingXXMedium,
                                    bottom = paddingSmall
                                ),
                                text = stringResource(id = R.string.library_playlists_title),
                                style = h3Tv,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        items(
                            count = playLists.itemCount,
                            key = playLists.itemKey(),
                            contentType = playLists.itemContentType()
                        ) { index ->
                            val playListEntity =
                                playLists[index]
                            playListEntity?.let {
                                PlaylistView(
                                    modifier = Modifier.padding(horizontal = paddingMedium),
                                    focusRequester = playlistFocusRequesters[index],
                                    onFocused = {
                                        viewModel.onFocusedPlayList(ListSelectionType.PlayList(playListEntity))
                                    },
                                    onClick = {
                                        isMenuVisible = false
                                        if (videoListItems.itemCount == 0) {
                                            refreshFocusRequester.requestFocus()
                                        } else {
                                            videoGridFocusRequester.requestFocus()
                                        }
                                    },
                                    title = playListEntity.title,
                                    imageUrl = playListEntity.thumbnail
                                )
                            }
                        }

                        if ((playLists.loadState.refresh is LoadState.Loading).not() && playLists.itemSnapshotList.isEmpty()) {
                            item {
                                Text(
                                    modifier = Modifier.padding(horizontal = paddingXXMedium),
                                    text = stringResource(id = R.string.no_playlists_yet),
                                    color = enforcedCloud,
                                    style = labelRegularTv,
                                )
                            }
                        }
                    }
                }

                LaunchedEffect(isMenuVisible, previousIsMenuVisible) {
                    if (isMenuVisible && (previousIsMenuVisible == false)) {
                        focusRequester.requestFocus()
                    }
                }
            }

            Box(
                modifier = Modifier
                    .onKeyEvent { keyEvent ->
                        when {
                            keyEvent.key == Key.Back -> {
                                isMenuVisible = true
                                true
                            }

                            keyEvent.key == Key.DirectionRight && isMenuVisible -> {
                                isMenuVisible = false
                                true
                            }

                            keyEvent.type == KeyEventType.KeyDown
                                    && keyEvent.key == Key.DirectionLeft
                                    && !isMenuVisible
                                    && (
                                    (focusedElement == VideoGrid && focusedIndex % 3 == 0)
                                            || focusedElement == RefreshButton
                                    ) -> {
                                isMenuVisible = true
                                true
                            }

                            else -> false
                        }
                    }
                    .fillMaxSize()
                    .clipToBounds(),
                contentAlignment = Alignment.Center
            ) {

                val title = when (val it = state.selectedList) {
                    is ListSelectionType.Library -> getLibraryCollectionTitle(libraryCollection = it.libraryCollection)
                    is ListSelectionType.PlayList -> it.playListEntity.title
                }

                ConstraintLayout(
                    modifier = Modifier
                        .wrapContentWidth(unbounded = true, align = Alignment.Start)
                        .padding(top = paddingXXMedium, start = browseGridColumnStartPadding)
                ) {
                    val (titleText, refreshButton, descriptionText, playAllButton, shuffleAllButton, verticalGrid, gridOverlay) = createRefs()

                    Text(
                        modifier = Modifier
                            .constrainAs(titleText) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                            }
                            .widthIn(max = tvLibraryTitleTextMaxWidth),
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = enforcedWhite,
                        style = h3Tv,
                    )

                    Button(
                        modifier = Modifier
                            .constrainAs(refreshButton) {
                                start.linkTo(titleText.end, margin = paddingXSmall)
                                top.linkTo(titleText.top)
                                bottom.linkTo(titleText.bottom)
                            }
                            .size(refreshButtonSize)
                            .onFocusChanged {
                                if (it.hasFocus || it.isFocused) {
                                    focusedElement = RefreshButton
                                }
                            }
                            .focusRequester(refreshFocusRequester),
                        onClick = {
                            viewModel.onFocusVideo(null)
                            videoListItems.refresh()
                        },
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.colors(
                            contentColor = enforcedBone,
                            focusedContentColor = enforcedDarkmo,
                            containerColor = enforcedWhite.copy(alpha = 0.1f),
                            focusedContainerColor = rumbleGreen
                        )
                    ) {
                        Icon(
                            modifier = Modifier.size(refreshButtonIconSize),
                            painter = painterResource(id = R.drawable.ic_refresh),
                            contentDescription = stringResource(id = R.string.refresh),
                        )
                    }

                    val subHeadingVisible = state.selectedList is ListSelectionType.PlayList
                    (state.selectedList as? ListSelectionType.PlayList)?.let {
                        val subHeaderText =
                            pluralStringResource(
                                id = R.plurals.videos_count,
                                count = it.playListEntity.videosQuantity,
                                it.playListEntity.videosQuantity
                            )

                        Row(modifier = Modifier
                            .constrainAs(descriptionText) {
                                top.linkTo(titleText.bottom, paddingXXSmall)
                                start.linkTo(parent.start)
                            }
                            .widthIn(max = tvLibrarySubHeaderMaxWidth)
                            .wrapContentWidth()
                        ) {
                            Text(
                                modifier = Modifier
                                    .weight(1f, fill = false),
                                text = stringResource(R.string.library_header_playlist_by) + (it.playListEntity.channelName
                                    ?: it.playListEntity.username),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = enforcedBone,
                                style = labelRegularTv
                            )

                            Text(
                                text = pluralStringResource(
                                    id = R.plurals.videos_count,
                                    count = it.playListEntity.videosQuantity,
                                    it.playListEntity.videosQuantity
                                ),
                                maxLines = 1,
                                color = enforcedBone,
                                style = labelRegularTv
                            )
                        }

                    }

                    val libraryCollection = (state.selectedList as? ListSelectionType.Library)?.libraryCollection
                    if (videoListItems.itemCount > 0 &&
                        libraryCollection != LibraryCollection.WatchHistory &&
                        libraryCollection != LibraryCollection.WatchLater
                    ) {
                        Button(
                            modifier = Modifier
                                .height(tvButtonHeightSmall)
                                .constrainAs(playAllButton) {
                                    top.linkTo(parent.top, margin = paddingSmall)
                                    bottom.linkTo((if (subHeadingVisible) descriptionText else titleText).bottom)
                                    end.linkTo(shuffleAllButton.start, margin = paddingXXSmall)
                                }
                                .onFocusChanged {
                                    if (it.hasFocus || it.isFocused) {
                                        focusedElement = PlayAllButton
                                    }
                                },
                            contentPadding = PaddingValues(vertical = 0.dp, horizontal = paddingSmall),
                            onClick = { onNavigateToPlayAll(title, videoListItems.itemSnapshotList.items, false) },
                            colors = ButtonDefaults.colors(
                                contentColor = enforcedWhite,
                                focusedContentColor = enforcedDarkmo,
                                containerColor = enforcedWhite.copy(alpha = 0.12f),
                                focusedContainerColor = rumbleGreen
                            )
                        ) {
                            Text(
                                text = stringResource(id = R.string.play_all),
                                style = labelRegularTv
                            )
                        }

                        Button(
                            modifier = Modifier
                                .height(tvButtonHeightSmall)
                                .constrainAs(shuffleAllButton) {
                                    top.linkTo(parent.top, margin = paddingSmall)
                                    bottom.linkTo((if (subHeadingVisible) descriptionText else titleText).bottom)
                                    end.linkTo(verticalGrid.end)
                                }
                                .onFocusChanged {
                                    if (it.hasFocus || it.isFocused) {
                                        focusedElement = ShuffleAllButton
                                    }
                                },
                            contentPadding = PaddingValues(vertical = 0.dp, horizontal = paddingSmall),
                            onClick = { onNavigateToPlayAll(title, videoListItems.itemSnapshotList.items, true) },
                            colors = ButtonDefaults.colors(
                                contentColor = enforcedWhite,
                                focusedContentColor = enforcedDarkmo,
                                containerColor = enforcedWhite.copy(alpha = 0.12f),
                                focusedContainerColor = rumbleGreen
                            )
                        ) {
                            Text(
                                text = stringResource(id = R.string.shuffle_all),
                                style = labelRegularTv
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .padding(top = paddingMedium)
                            .wrapContentWidth(unbounded = true, align = Alignment.Start)
                            .constrainAs(verticalGrid) {
                                top.linkTo((if (subHeadingVisible) descriptionText else titleText).bottom)
                                start.linkTo(parent.start)
                            }
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        TvLazyVerticalGrid(
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentWidth(unbounded = true, align = Alignment.Start)
                                .width((videoCardWidth + paddingSmall) * 3)
                                .onFocusChanged {
                                    if (it.hasFocus || it.isFocused) {
                                        focusedElement = VideoGrid
                                    }
                                }
                                .focusProperties {
                                    enter = {
                                        videoGridFocusRequesters[state.focusedVideo?.uuid] ?: FocusRequester.Default
                                    }
                                }
                                .focusRequester(videoGridFocusRequester)
                                .padding(bottom = tvLazyVerticalGridBottomPadding),
                            columns = TvGridCells.Fixed(TV_LIBRARY_SCREEN_GRID_WIDTH),
                            pivotOffsets = PivotOffsets(parentFraction = 0.5f, childFraction = 0.5f),
                            verticalArrangement = Arrangement.spacedBy(paddingXLarge),
                            horizontalArrangement = Arrangement.spacedBy(paddingSmall)
                        ) {
                            items(
                                count = videoListItems.itemCount,
                                key = videoListItems.itemKey(),
                                contentType = videoListItems.itemContentType()
                            ) { index ->
                                val item = videoListItems[index]
                                item?.let { entity ->
                                    if (entity is VideoEntity) {
                                        val focusRequester =
                                            videoGridFocusRequesters.getOrPut(entity.uuid) { FocusRequester() }

                                        VideoCard(
                                            videoEntity = entity,
                                            onFocused = {
                                                focusedIndex = index
                                                viewModel.onFocusVideo(entity)
                                            },
                                            onSelected = {
                                                onNavigateToVideoPlayer(entity)
                                            },
                                            focusRequester = focusRequester
                                        )
                                    }
                                }
                            }

                            videoListItems.apply {
                                when (loadState.append) {
                                    is LoadState.Loading -> {
                                        item(span = { TvGridItemSpan(3) }) {
                                            PageLoadingView(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .wrapContentHeight()
                                                    .padding(paddingMedium)
                                            )
                                        }
                                    }

                                    is LoadState.Error -> {
                                        item(span = { TvGridItemSpan(3) }) {
                                            ErrorView(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .wrapContentHeight()
                                                    .padding(paddingMedium),
                                            )
                                        }
                                    }

                                    else -> {}
                                }
                                }
                            }
                        }
                    }

                    videoListItems.apply {
                        when {
                            loadState.refresh is LoadState.NotLoading && this.itemCount == 0 -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(paddingMedium),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(
                                            id = if (
                                                (state.selectedList as? ListSelectionType.Library)?.libraryCollection != LibraryCollection.Purchases
                                            )
                                                R.string.empty_playlist_message
                                            else
                                                R.string.empty_purchase_playlist_message,
                                        ),
                                        color = enforcedWhite
                                    )
                                }
                            }

                            loadState.refresh is LoadState.Loading && this.itemCount == 0 -> {
                                RumbleProgressIndicator()
                            }

                            loadState.refresh is LoadState.Error -> {
                                ErrorView(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(paddingMedium),
                                )
                            }
                        }
                    }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun PlaylistView(
    modifier: Modifier,
    onFocused: () -> Unit,
    onClick: () -> Unit,
    focusRequester: FocusRequester,
    title: String,
    showImage: Boolean = true,
    imageUrl: String = "",
) {
    var isFocused by remember { mutableStateOf(false) }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(browseLeftNavMenuItemHeight)
            .padding(top = paddingXXXSmall)
            .focusRequester(focusRequester)
            .onFocusChanged {
                isFocused = it.isFocused
                if (it.isFocused) {
                    onFocused()
                }
            }
            .focusable()
            .clickable { onClick() }
            .background(
                color = if (isFocused) enforcedWhite.copy(alpha = .1f) else Color.Transparent,
                shape = RoundedCornerShape(radiusXMedium)
            ),
        colors = SurfaceDefaults.colors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (showImage) {
                Spacer(modifier = Modifier.width(paddingXXXSmall))
                Box {
                    Image(
                        modifier = Modifier.size(dimensionResource(id = R.dimen.menu_headers_icon_size)),
                        painter = painterResource(id = R.drawable.ic_playlist_place_holder),
                        contentDescription = title
                    )
                    AsyncImage(
                        modifier = Modifier
                            .size(dimensionResource(id = R.dimen.menu_headers_icon_size))
                            .clip(CircleShape),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = title,
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Text(
                modifier = Modifier.padding(start = paddingXSmall),
                text = title,
                color = if (isFocused) rumbleGreen else MaterialTheme.colorScheme.onSurface,
                style = labelBoldTv,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun NotLoggedIn(
    focusRequester: FocusRequester,
    onLogin: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .background(Color.Transparent),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = stringResource(id = R.string.library_not_logged_in_message),
            color = enforcedWhite,
            style = h2Tv,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(paddingLarge))

        Image(
            painter = painterResource(id = R.drawable.v3_ic_subscriptions_user_not_logged_in),
            contentDescription = stringResource(id = R.string.library_not_logged_in_message)
        )

        Spacer(modifier = Modifier.height(paddingLarge))

        var loginButtonFocused by remember { mutableStateOf(true) }
        Button(
            modifier = Modifier
                .size(width = 310.dp, height = 44.dp)
                .onFocusEvent { loginButtonFocused = it.isFocused },
            onClick = onLogin,
            colors = ButtonDefaults.colors(
                contentColor = enforcedWhite,
                focusedContentColor = enforcedDarkmo,
                containerColor = enforcedWhite.copy(alpha = 0.1f),
                focusedContainerColor = rumbleGreen
            )
        ) {
            Text(
                modifier = Modifier.fillMaxSize(),
                text = stringResource(id = R.string.subscriptions_login_button_label),
                textAlign = TextAlign.Center,
                style = h3Tv
            )
        }
    }
}

@Composable
private fun getLibraryCollectionTitle(libraryCollection: LibraryCollection): String =
    when (libraryCollection) {
        LibraryCollection.WatchHistory -> stringResource(id = R.string.watch_history)
        LibraryCollection.WatchLater -> stringResource(id = R.string.watch_later)
        LibraryCollection.Liked -> stringResource(id = R.string.liked_videos)
        LibraryCollection.Purchases -> stringResource(id = R.string.purchases)
    }

@Composable
private fun rememberPreviousState(value: Boolean): Boolean? {
    val previousState = remember { mutableStateOf<Boolean?>(null) }
    LaunchedEffect(key1 = value) {
        previousState.value = value
    }
    return previousState.value
}
