@file:OptIn(ExperimentalTvMaterial3Api::class, ExperimentalComposeUiApi::class)

package com.rumble.player

import android.view.Gravity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindowProvider
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.rumble.R
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import com.rumble.domain.library.domain.model.PlayListVisibility
import com.rumble.theme.RumbleTvTypography.h3Tv
import com.rumble.theme.RumbleTvTypography.h5Tv
import com.rumble.theme.RumbleTvTypography.labelRegularTv
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXMedium
import com.rumble.theme.radiusSmall
import com.rumble.theme.rumbleGreen
import com.rumble.theme.tvCheckboxSize
import com.rumble.theme.tvPlayerModalWidth
import com.rumble.theme.tvPrivatePlaylistLockIconSize
import java.util.UUID

private sealed class Focusable {
    object Back : Focusable()
    data class List(val uuid: UUID) : Focusable()
}

@Composable
fun SaveToPlaylistDialog(
    viewModel: SaveToPlaylistHandler,
    onShowAlertDialog: (String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val playlists: LazyPagingItems<PlayListEntity> = state.playlists.collectAsLazyPagingItems()

    var focusedElement: Focusable by remember { mutableStateOf(Focusable.Back) }

    val (backFocusRequester) = remember { FocusRequester.createRefs() }

    val listFocusRequesters = remember { mutableMapOf<UUID, FocusRequester>() }

    val updatedEntity by viewModel.updatedPlaylist.collectAsStateWithLifecycle()

    updatedEntity?.let { updated ->
        when (updated) {
            is UpdatePlaylist.VideoAddedToPlaylist -> playlists.itemSnapshotList.find { playListEntity ->
                playListEntity?.id.equals(
                    updated.playlistId
                )
            }?.videos?.add(
                updated.playlistVideoEntity
            )

            is UpdatePlaylist.VideoRemovedFromPlaylist -> playlists.itemSnapshotList.find { playListEntity ->
                playListEntity?.id.equals(
                    updated.playlistId
                )
            }?.videos?.removeIf { video -> video.id == updated.videoId }
        }
    }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect {
            when (it) {
                SaveToPlaylistVmEvent.Error ->
                    onShowAlertDialog(context.getString(R.string.generic_error_message))

                SaveToPlaylistVmEvent.ErrorVideoAlreadyAdded ->
                    onShowAlertDialog(context.getString(R.string.video_already_added_to_playlist))
            }
        }
    }

    LaunchedEffect(Unit) {
        state.watchLaterPlaylistEntity?.uuid?.let {
            listFocusRequesters[it]?.requestFocus()
        }
    }

    Dialog(
        onDismissRequest = { viewModel.onDismiss() }) {
        val dialogWindowProvider = LocalView.current.parent as DialogWindowProvider
        dialogWindowProvider.window.setGravity(Gravity.END)

        ConstraintLayout(
            modifier =
            Modifier
                .padding(paddingSmall)
                .fillMaxHeight()
                .wrapContentWidth()
        ) {
            val (back, window) = createRefs()

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(tvPlayerModalWidth)
                    .constrainAs(window) {
                        start.linkTo(back.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .background(
                        color = enforcedDarkmo,
                        shape = RoundedCornerShape(radiusSmall)
                    )
            ) {


                Text(
                    modifier = Modifier.padding(
                        top = paddingSmall,
                        start = paddingSmall,
                        end = paddingSmall,
                        bottom = paddingXXMedium
                    ),
                    text = stringResource(id = R.string.save_to_title),
                    style = h3Tv,
                    color = enforcedWhite
                )

                TvLazyColumn(
                    modifier = Modifier
                        .focusProperties {
                            exit = { focusDirection ->
                                if (focusDirection == FocusDirection.Left || focusDirection == FocusDirection.Up) backFocusRequester else FocusRequester.Default
                            }
                        }
                        .padding(horizontal = paddingMedium),
                    verticalArrangement = Arrangement.spacedBy(paddingSmall),
                ) {

                    state.watchLaterPlaylistEntity?.let { item ->
                        item {
                            val focusRequester =
                                listFocusRequesters.getOrPut(item.uuid) { FocusRequester() }

                            val videoIds = item.videos.map { video -> video.id }
                            val saved = state.videoEntity?.id?.let { videoIds.contains(it) } ?: false

                            PlayListItem(
                                title = item.title,
                                ownerName = if (item.channelName != null)
                                    stringResource(id = R.string.channel_name_prefix) + item.channelName
                                else
                                    stringResource(id = R.string.username_prefix) + item.username,
                                visibility = item.visibility,
                                focusedElement = focusedElement,
                                uuid = item.uuid,
                                focusRequester = focusRequester,
                                saved = saved,
                                onRemoveFromPlaylist = { viewModel.onRemoveFromPlaylist(item.id) },
                                onSaveToPlaylist = {
                                    viewModel.onSaveToPlaylist(item.id)
                                },
                                onFocusableChange = { focusedElement = it }
                            )
                        }
                    }

                    items(
                        count = playlists.itemCount,
                        key = playlists.itemKey(),
                        contentType = playlists.itemContentType()
                    ) { index ->
                        playlists[index]?.let { item ->

                            val focusRequester =
                                listFocusRequesters.getOrPut(item.uuid) { FocusRequester() }

                            val videoIds = item.videos.map { video -> video.id }
                            val saved = state.videoEntity?.id?.let { videoIds.contains(it) } ?: false

                            PlayListItem(
                                title = item.title,
                                ownerName = if (item.channelName != null)
                                    stringResource(id = R.string.channel_name_prefix) + item.channelName
                                else
                                    stringResource(id = R.string.username_prefix) + item.username,
                                visibility = item.visibility,
                                focusedElement = focusedElement,
                                uuid = item.uuid,
                                focusRequester = focusRequester,
                                saved = saved,
                                onRemoveFromPlaylist = { viewModel.onRemoveFromPlaylist(item.id) },
                                onSaveToPlaylist = { viewModel.onSaveToPlaylist(item.id) },
                                onFocusableChange = { focusedElement = it }
                            )
                        }
                    }
                }
            }

            Icon(
                modifier = Modifier
                    .focusTarget()
                    .focusRequester(backFocusRequester)
                    .onFocusEvent { if (it.isFocused) focusedElement = Focusable.Back }
                    .constrainAs(back) {
                        end.linkTo(window.start)
                        top.linkTo(window.top)
                    }
                    .padding(paddingMedium)
                    .clickable {
                        viewModel.onDismiss()
                    },
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = stringResource(id = R.string.back),
                tint = if (focusedElement == Focusable.Back) rumbleGreen else enforcedWhite
            )
        }
    }
}

@Composable
private fun PlayListItem(
    title: String,
    ownerName: String?,
    visibility: PlayListVisibility,
    focusedElement: Focusable,
    uuid: UUID,
    focusRequester: FocusRequester,
    saved: Boolean,
    onRemoveFromPlaylist: () -> Unit,
    onSaveToPlaylist: () -> Unit,
    onFocusableChange: (Focusable) -> Unit,
) {
    Row(
        modifier = Modifier
            .background(
                color = if (focusedElement == Focusable.List(uuid)) enforcedWhite.copy(
                    alpha = .1f
                ) else Color.Transparent,
                shape = RoundedCornerShape(radiusSmall)
            )
            .wrapContentHeight()
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusEvent {
                if (it.isFocused || it.hasFocus) {
                    onFocusableChange(Focusable.List(uuid))
                }
            }
            .clickable {
                if (saved)
                    onRemoveFromPlaylist()
                else
                    onSaveToPlaylist()
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Image(
            modifier = Modifier
                .padding(horizontal = paddingSmall, vertical = paddingXSmall)
                .size(tvCheckboxSize),
            painter = painterResource(id = if (saved) R.drawable.ic_checkbox_checked else R.drawable.ic_checkbox_unchecked),
            contentDescription = stringResource(id = if (saved) R.string.description_saved else R.string.description_not_saved)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = h5Tv,
                color = enforcedWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            ownerName?.let {
                Text(
                    text = it,
                    style = labelRegularTv,
                    color = enforcedWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Spacer(modifier = Modifier.width(paddingSmall))

        if (visibility == PlayListVisibility.PRIVATE)
            Image(
                modifier = Modifier
                    .size(tvPrivatePlaylistLockIconSize),
                painter = painterResource(id = R.drawable.ic_lock),
                contentDescription = stringResource(id = R.string.playlist_private)
            )
        Spacer(modifier = Modifier.width(paddingSmall))
    }
}