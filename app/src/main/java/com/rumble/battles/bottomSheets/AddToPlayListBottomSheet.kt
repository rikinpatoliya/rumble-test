package com.rumble.battles.bottomSheets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.rumble.battles.R
import com.rumble.battles.commonViews.MainActionButton
import com.rumble.battles.commonViews.PageLoadingView
import com.rumble.battles.commonViews.RumbleTextActionButton
import com.rumble.battles.library.presentation.playlist.AddToPlayListHandler
import com.rumble.battles.library.presentation.playlist.UpdatePlaylist
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import com.rumble.domain.library.domain.model.PlayListVisibility
import com.rumble.network.queryHelpers.PlayListType
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTypography
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.imageXSmall
import com.rumble.theme.imageXXSmall
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.radiusMedium
import com.rumble.theme.radiusXSmall
import com.rumble.theme.rumbleGreen

@Composable
fun AddToPlayListBottomSheet(
    videoId: Long,
    addToPlayListHandler: AddToPlayListHandler,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val state by addToPlayListHandler.addToPlayListState.collectAsStateWithLifecycle()
    val playLists: LazyPagingItems<PlayListEntity> =
        state.availablePlayLists.collectAsLazyPagingItems()
    val scrollState = rememberLazyListState()
    val updatedEntity by addToPlayListHandler.updatedPlaylist.collectAsStateWithLifecycle()

    updatedEntity?.let { updated ->
        when (updated) {
            is UpdatePlaylist.VideoAddedToPlaylist -> playLists.itemSnapshotList.find { playListEntity ->
                playListEntity?.id.equals(
                    updated.playlistId
                )
            }?.let {
                it.videos.add( updated.playlistVideoEntity)
                it.videoIds?.add(updated.playlistVideoEntity.id)
            }

            is UpdatePlaylist.VideoRemovedFromPlaylist -> playLists.itemSnapshotList.find { playListEntity ->
                playListEntity?.id.equals(
                    updated.playlistId
                )
            }?.let {
                it.videos.removeIf { video -> video.id == updated.videoId }
                it.videoIds?.removeIf { id -> id == updated.videoId }
            }
        }
    }

    Box(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .clip(RoundedCornerShape(topStart = radiusMedium, topEnd = radiusMedium))
            .background(RumbleCustomTheme.colors.surface)
    ) {
        var spacerHeight by remember { mutableIntStateOf(0) }
        val density = LocalContext.current.resources.displayMetrics.density
        Column {
            Row(
                modifier = Modifier
                    .padding(paddingMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = context.getString(R.string.save_to),
                    color = RumbleCustomTheme.colors.primary,
                    style = RumbleTypography.h3
                )
                Spacer(modifier = Modifier.weight(1F))
                RumbleTextActionButton(
                    text = context.getString(R.string.plus_new_playlist),
                    textStyle = h6,
                    textColor = RumbleCustomTheme.colors.primary,
                    onClick = { addToPlayListHandler.onCreateNewPlayList(videoId) }
                )
            }
            LazyColumn(
                state = scrollState,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                playLists.apply {
                    if (loadState.refresh is LoadState.Loading) {
                        item {
                            PageLoadingView(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(paddingMedium)
                            )
                        }
                    } else {
                        item {
                            val inPlayList =
                                addToPlayListHandler.getIsVideoInPlayList(state.watchLaterPlayList, videoId)
                            PlayListSelectionRow(
                                entity = state.watchLaterPlayList,
                                checked = inPlayList,
                                onToggleCheckedState = {
                                    addToPlayListHandler.onToggleVideoInPlayList(
                                        inPlayList = inPlayList,
                                        playListId = state.watchLaterPlayList.id,
                                        videoId = videoId
                                    )
                                }
                            )
                            if (playLists.itemCount > 0) {
                                Divider(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = RumbleCustomTheme.colors.backgroundHighlight
                                )
                            }
                        }
                        if (playLists.itemCount > 0) {
                            items(count = playLists.itemCount) { index ->
                                val item = playLists[index]
                                item?.let { entity ->
                                    val inPlayList = addToPlayListHandler.getIsVideoInPlayList(
                                        entity,
                                        videoId
                                    )
                                    if (addToPlayListHandler.canSaveToPlayList(entity)) {
                                        PlayListSelectionRow(
                                            entity = entity,
                                            checked = inPlayList,
                                            onToggleCheckedState = {
                                                addToPlayListHandler.onToggleVideoInPlayList(
                                                    inPlayList = inPlayList,
                                                    playListId = entity.id,
                                                    videoId = videoId
                                                )
                                            }
                                        )
                                    }
                                }
                                if (playLists.itemCount - 1 != index) {
                                    Divider(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = RumbleCustomTheme.colors.backgroundHighlight
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height((spacerHeight * 1.5 / density).dp))
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(RumbleCustomTheme.colors.surface)
                .padding(paddingMedium)
                .align(Alignment.BottomCenter)
                .onGloballyPositioned {
                    spacerHeight = it.size.height
                }
        ) {
            MainActionButton(
                modifier = Modifier.fillMaxWidth(),
                textModifier = Modifier.padding(top = paddingSmall, bottom = paddingSmall),
                text = context.getString(R.string.done),
                backgroundColor = RumbleCustomTheme.colors.backgroundHighlight,
                onClick = onClose
            )
        }
    }
}

@Composable
fun PlayListSelectionRow(
    entity: PlayListEntity,
    checked: Boolean,
    onToggleCheckedState: (Boolean) -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingMedium),
    ) {
        val (checkBox, title, icon) = createRefs()
        Box(
            modifier = Modifier
                .constrainAs(checkBox) {
                    start.linkTo(parent.start)
                    end.linkTo(title.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .clip(RoundedCornerShape(radiusXSmall))
                .clickable {
                    onToggleCheckedState(!checked)
                }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_rectangle_box),
                contentDescription = if (checked) stringResource(id = R.string.checked) else stringResource(
                    id = R.string.unchecked
                ),
                modifier = Modifier
                    .size(imageXSmall),
                tint = if (checked) RumbleCustomTheme.colors.secondary else RumbleCustomTheme.colors.onSecondary,
            )
            if (checked) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_check),
                    contentDescription = stringResource(id = R.string.checked),
                    modifier = Modifier
                        .size(imageXSmall),
                    tint = rumbleGreen
                )
            }
        }
        Column(
            modifier = Modifier
                .constrainAs(title) {
                    start.linkTo(checkBox.end)
                    end.linkTo(icon.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                }
                .padding(vertical = paddingMedium, horizontal = paddingXSmall)
        ) {
            Text(
                text = entity.title,
                style = h4,
                color = RumbleCustomTheme.colors.primary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
            if (entity.id != PlayListType.WATCH_LATER.id)
                Text(
                    text = if (entity.channelName != null)
                        stringResource(id = R.string.channel_name_prefix) + entity.channelName
                    else
                        stringResource(id = R.string.username_prefix) + entity.username,
                    style = h6,
                    color = RumbleCustomTheme.colors.secondary,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
        }
        if (entity.visibility == PlayListVisibility.PRIVATE) {
            Icon(
                painter = painterResource(id = R.drawable.ic_lock),
                contentDescription = stringResource(
                    id = R.string.private_visibility
                ),
                modifier = Modifier
                    .size(imageXXSmall)
                    .constrainAs(icon) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                tint = RumbleCustomTheme.colors.secondary
            )
        }
    }
}
