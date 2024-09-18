package com.rumble.battles.bottomSheets

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.battles.commonViews.BottomSheetItem
import com.rumble.battles.commonViews.RumbleBottomSheet
import com.rumble.domain.video.model.VideoOption
import com.rumble.network.queryHelpers.PlayListType

@Composable
fun VideoOptionsBottomSheet(
    videoOptions: List<VideoOption>,
    onSaveToPlaylist: () -> Unit,
    onSaveToWatchLater: () -> Unit,
    onRemoveFromPlayList: (String) -> Unit,
    onShare: () -> Unit,
    onHideBottomSheet: () -> Unit
) {
    RumbleBottomSheet(
        sheetItems = getVideoOptionBottomSheetItems(
            videoOptions = videoOptions,
            onSaveToPlaylist = onSaveToPlaylist,
            onSaveToWatchLater = onSaveToWatchLater,
            onRemoveFromPlayList = onRemoveFromPlayList,
            onShare = onShare
        ),
        onCancel = onHideBottomSheet
    )
}

@Composable
fun getVideoOptionBottomSheetItems(
    videoOptions: List<VideoOption>,
    onSaveToPlaylist: () -> Unit,
    onSaveToWatchLater: () -> Unit,
    onRemoveFromPlayList: (String) -> Unit,
    onShare: () -> Unit,
): List<BottomSheetItem> {
    val result = mutableListOf<BottomSheetItem>()
    videoOptions.forEach { option ->
        result.add(
            when (option) {
                is VideoOption.RemoveFromPlayList -> {
                    BottomSheetItem(
                        imageResource = R.drawable.ic_trash,
                        text = stringResource(R.string.remove_from_playlist),
                        action = { onRemoveFromPlayList(option.playListId) }
                    )
                }

                VideoOption.RemoveFromWatchHistory -> {
                    BottomSheetItem(
                        imageResource = R.drawable.ic_trash,
                        text = stringResource(R.string.remove_from_watch_history),
                        action = { onRemoveFromPlayList(PlayListType.WATCH_HISTORY.toString()) }
                    )
                }

                VideoOption.RemoveFromWatchLater -> {
                    BottomSheetItem(
                        imageResource = R.drawable.ic_trash,
                        text = stringResource(R.string.remove_from_watch_later),
                        action = { onRemoveFromPlayList(PlayListType.WATCH_LATER.toString()) }
                    )
                }

                VideoOption.SaveToPlayList -> {
                    BottomSheetItem(
                        imageResource = R.drawable.ic_save_playlist,
                        text = stringResource(R.string.save_to_playlist),
                        action = onSaveToPlaylist
                    )
                }

                VideoOption.SaveToWatchLater -> {
                    BottomSheetItem(
                        imageResource = R.drawable.ic_time,
                        text = stringResource(R.string.save_to_watch_later),
                        action = onSaveToWatchLater
                    )
                }

                VideoOption.Share -> {
                    BottomSheetItem(
                        imageResource = R.drawable.ic_share,
                        text = stringResource(R.string.share),
                        action = onShare
                    )
                }
            }
        )
    }
    return result
}