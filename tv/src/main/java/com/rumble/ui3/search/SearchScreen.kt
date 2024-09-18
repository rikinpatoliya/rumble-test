@file:OptIn(ExperimentalTvMaterial3Api::class, ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)

package com.rumble.ui3.search

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rumble.R
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.discover.domain.domainmodel.CategoryEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.theme.RumbleTvTypography.labelRegularTv
import com.rumble.theme.channelCardBorderWidth
import com.rumble.theme.channelCardCornerRadius
import com.rumble.theme.channelCardPlaceholderHeight
import com.rumble.theme.channelCardPlaceholderWidth
import com.rumble.theme.enforcedBone
import com.rumble.theme.enforcedCloud
import com.rumble.theme.enforcedLite
import com.rumble.theme.enforcedWhite
import com.rumble.theme.imageSmall
import com.rumble.theme.imageXSmall
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXXMedium
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusXXSmall
import com.rumble.theme.radiusXXXSmall
import com.rumble.theme.rumbleGreen
import com.rumble.theme.searchSuggestionPlaceholderHeight
import com.rumble.theme.searchSuggestionPlaceholderWidth
import com.rumble.ui3.common.composables.ChannelCard
import com.rumble.ui3.library.VideoCard
import com.rumble.utils.extension.conditional
import java.util.UUID

@Composable
fun SearchScreen(
    viewModel: SearchHandler,
    focusRequester: FocusRequester,
    onNavigateToCategory: (CategoryEntity) -> Unit,
    onNavigateToVideo: (VideoEntity) -> Unit,
    onNavigateToChannel: (ChannelDetailsEntity) -> Unit,
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    val videos = state.videoResults.collectAsLazyPagingItems()
    val channels = state.channelResults.collectAsLazyPagingItems()

    val suggestionFocusRequesters = List(size = state.recentQueries.size + state.categories.size) { FocusRequester() }
    val videoFocusRequesters = mutableMapOf<UUID, FocusRequester>()
    val channelFocusRequesters = remember { mutableMapOf<UUID, FocusRequester>() }

    val suggestionRowFocusRequester = remember { FocusRequester() }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val (searchInput, grid) = createRefs()

        SearchInputField(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = paddingXXMedium)
                .constrainAs(searchInput) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                },
            onValueChange = { viewModel.onQueryChanged(it) },
            value = state.query,
            focusRequester = focusRequester,
        )

        if (state.initialLoadState.not() && (state.categoriesLoading
                    || videos.loadState.source.refresh is LoadState.Loading
                    || channels.loadState.source.refresh is LoadState.Loading)
        ) {
            LoadingView(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = paddingXXMedium)
                    .constrainAs(grid) {
                        top.linkTo(searchInput.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        height = Dimension.fillToConstraints
                    },
            )
        } else if (state.query.isNotEmpty() && state.recentQueries.isEmpty() && state.categories.isEmpty() && videos.itemSnapshotList.isEmpty() && channels.itemSnapshotList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingMedium),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.no_search_results, state.query),
                    color = enforcedWhite
                )
            }
        } else if (state.query.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = paddingXXMedium)
                    .constrainAs(grid) {
                        top.linkTo(searchInput.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        height = Dimension.fillToConstraints
                    },
            ) {

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusProperties {
                            enter = {
                                suggestionFocusRequesters[state.focusedSuggestionIndex]
                            }
                        }
                        .focusRequester(suggestionRowFocusRequester)
                        .focusGroup(),
                    contentPadding = PaddingValues(horizontal = paddingXXMedium)
                ) {
                    itemsIndexed(state.recentQueries) { index, item ->
                        var focused by remember { mutableStateOf(false) }
                        AutoCompleteItem(
                            focusRequester = suggestionFocusRequesters[index],
                            text = item.query,
                            query = state.query,
                            onClick = {
                                viewModel.onContentSelected()
                                viewModel.onQueryChanged(item.query)
                            },
                            focused = focused,
                            onFocusChanged = {
                                focused = it.isFocused
                                if (focused) {
                                    viewModel.onFocusedSuggestion(index)
                                }
                            }
                        )
                    }

                    itemsIndexed(state.categories)
                    { index, item ->
                        var focused by remember { mutableStateOf(false) }
                        AutoCompleteItem(
                            focusRequester = suggestionFocusRequesters[index + state.recentQueries.size],
                            text = item.title,
                            query = state.query,
                            iconUrl = item.thumbnail,
                            focused = focused,
                            onClick = {
                                viewModel.onContentSelected()
                                onNavigateToCategory(item)
                            },
                            onFocusChanged = {
                                focused = it.isFocused
                                if (focused) {
                                    viewModel.onFocusedSuggestion(index + state.recentQueries.size)
                                }
                            }
                        )
                    }
                }

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = paddingSmall)
                        .focusProperties {
                            enter = {
                                videoFocusRequesters[state.focusedVideo?.uuid]
                                    ?: videoFocusRequesters[videos.itemSnapshotList[0]?.uuid]
                                    ?: FocusRequester.Default
                            }
                        },
                    horizontalArrangement = Arrangement.spacedBy(paddingMedium),
                    contentPadding = PaddingValues(horizontal = paddingXXMedium)
                ) {

                    items(
                        count = videos.itemCount,
                        key = videos.itemKey(),
                        contentType = videos.itemContentType()
                    ) { index ->
                        videos[index]?.let { video ->
                            val videoFocusRequester =
                                videoFocusRequesters.getOrPut(video.uuid) {
                                    FocusRequester()
                                }

                            VideoCard(
                                videoEntity = video,
                                onFocused = { viewModel.onFocusVideo(video) },
                                onSelected = {
                                    viewModel.onContentSelected()
                                    onNavigateToVideo(video)
                                },
                                focusRequester = videoFocusRequester
                            )
                        }
                    }
                }

                LazyRow(
                    modifier = Modifier
                        .focusProperties {
                            enter = {
                                channelFocusRequesters[state.focusedChannel?.uuid]
                                    ?: videoFocusRequesters[channels.itemSnapshotList[0]?.uuid]
                                    ?: FocusRequester.Default
                            }
                        }
                        .fillMaxWidth()
                        .padding(top = paddingXXMedium),
                    contentPadding = PaddingValues(horizontal = paddingXXMedium),
                    horizontalArrangement = Arrangement.spacedBy(paddingMedium)
                ) {
                    items(
                        count = channels.itemCount,
                        key = channels.itemKey(),
                        contentType = channels.itemContentType()
                    ) { index ->
                        channels[index]?.let { channel ->
                            ChannelCard(
                                thumbnail = channel.thumbnail,
                                channelTitle = channel.channelTitle,
                                verified = channel.verifiedBadge,
                                followers = channel.followers,
                                isFollowed = channel.followed,
                                focusRequester = channelFocusRequesters.getOrPut(channel.uuid) { FocusRequester() },
                                onClick = {
                                    viewModel.onContentSelected()
                                    onNavigateToChannel(channel)
                                },
                                onFocused = { viewModel.onFocusedChannel(channel) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchInputField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    BackHandler {
        hideKeyboard(context)
        focusManager.clearFocus()
    }

    TextField(
        value = value,
        onValueChange = { onValueChange(it) },
        singleLine = true,
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.v3_ic_search),
                contentDescription = stringResource(id = R.string.hint_search_videos_channels),
                tint = enforcedBone
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary,
            unfocusedTextColor = enforcedWhite,
            focusedIndicatorColor = enforcedWhite,
            unfocusedIndicatorColor = enforcedWhite,
            focusedTextColor = enforcedWhite
        ),
        placeholder = {
            Text(
                text = stringResource(id = R.string.hint_search),
                style = labelRegularTv.copy(enforcedWhite.copy(alpha = 0.5f))
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                hideKeyboard(context)
                focusManager.clearFocus()
            },
            onPrevious = {
                // required for FireTV to hide keyboard on back pressed
                hideKeyboard(context)
                focusManager.clearFocus()
            }
        ),
        modifier = modifier.focusRequester(focusRequester)
    )
}

@Composable
private fun AutoCompleteItem(
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester,
    text: String,
    query: String,
    iconUrl: String? = null,
    focused: Boolean,
    onClick: () -> Unit,
    onFocusChanged: (FocusState) -> Unit,
) {

    Row(
        modifier = modifier
            .wrapContentSize()
            .conditional(condition = focused) {
                border(
                    width = channelCardBorderWidth,
                    color = rumbleGreen,
                    shape = RoundedCornerShape(channelCardCornerRadius)
                )
            }
            .padding(paddingXXSmall)
            .background(
                color = enforcedLite.copy(alpha = 0.15f),
                RoundedCornerShape(radiusXXXSmall)
            )
            .focusRequester(focusRequester)
            .onFocusChanged {
                onFocusChanged(it)
            }
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (iconUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(iconUrl)
                    .build(),
                contentDescription = text,
                modifier = Modifier
                    .padding(paddingXXXSmall)
                    .size(imageSmall)
                    .clip(
                        RoundedCornerShape(radiusXXXSmall)
                    ),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .padding(paddingXXXSmall)
                    .size(imageSmall),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier
                        .size(imageXSmall),
                    painter = painterResource(id = R.drawable.v3_ic_search),
                    contentDescription = stringResource(id = R.string.back),
                    tint = enforcedWhite
                )
            }
        }

        val highlightedText = buildHighlightedText(text, query)
        Text(
            modifier = Modifier.padding(paddingXXXSmall),
            text = highlightedText,
            style = labelRegularTv,
            color = enforcedWhite
        )

        Icon(
            modifier = Modifier
                .padding(paddingXXXSmall),
            painter = painterResource(id = R.drawable.ic_arrow_top_left),
            contentDescription = stringResource(id = R.string.back),
            tint = enforcedCloud
        )
    }
}

fun buildHighlightedText(text: String, searchTerm: String): AnnotatedString {
    val lowercaseText = text.lowercase()
    val lowercaseSearchTerm = searchTerm.lowercase()

    return buildAnnotatedString {
        var startIndex = 0
        var foundIndex = lowercaseText.indexOf(lowercaseSearchTerm, startIndex)
        while (foundIndex != -1) {
            append(text.substring(startIndex, foundIndex))

            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(text.substring(foundIndex, foundIndex + lowercaseSearchTerm.length))
            }

            startIndex = foundIndex + lowercaseSearchTerm.length
            foundIndex = lowercaseText.indexOf(lowercaseSearchTerm, startIndex)
        }

        if (startIndex < text.length) {
            append(text.substring(startIndex))
        }
    }
}

@Composable
private fun LoadingView(modifier: Modifier) {
    Column(modifier = modifier.padding(start = paddingXXMedium)) {
        Row {
            PlaceholderSuggestionCard()
            PlaceholderSuggestionCard()
            PlaceholderSuggestionCard()
            PlaceholderSuggestionCard()
            PlaceholderSuggestionCard()
        }
        Spacer(modifier = Modifier.height(paddingXXMedium))
        Row {
            PlaceholderVideoCard()
            PlaceholderVideoCard()
            PlaceholderVideoCard()
            PlaceholderVideoCard()
        }
        Spacer(modifier = Modifier.height(paddingXXMedium))
        Row {
            PlaceholderChannelCard()
            PlaceholderChannelCard()
            PlaceholderChannelCard()
            PlaceholderChannelCard()
            PlaceholderChannelCard()
        }
    }
}

@Composable
fun PlaceholderSuggestionCard() {
    Box(
        modifier = Modifier
            .padding(end = paddingMedium)
            .width(searchSuggestionPlaceholderWidth)
            .height(searchSuggestionPlaceholderHeight)
            .background(enforcedWhite.copy(alpha = 0.18f), RoundedCornerShape(radiusXXSmall))
    )
}

@Composable
fun PlaceholderChannelCard() {
    Box(
        modifier = Modifier
            .padding(end = paddingMedium)
            .width(channelCardPlaceholderWidth)
            .height(channelCardPlaceholderHeight)
            .background(enforcedWhite.copy(alpha = 0.18f), RoundedCornerShape(channelCardCornerRadius))
    )
}

fun hideKeyboard(context: Context) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(null, 0)
}