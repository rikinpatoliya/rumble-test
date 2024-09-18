package com.rumble.battles.bottomSheets

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.battles.commonViews.BottomSheetItem
import com.rumble.battles.commonViews.RumbleBottomSheet
import com.rumble.domain.library.domain.model.PlayListOption

@Composable
fun PlayListOptionsBottomSheet(
    title: String? = null,
    subtitle: String? = null,
    bottomSheetItems: List<BottomSheetItem>,
    onHideBottomSheet: () -> Unit
) {
    RumbleBottomSheet(
        title = title,
        subtitle = subtitle,
        sheetItems = bottomSheetItems,
        onCancel = onHideBottomSheet
    )
}

@Composable
fun getPlayListOptionBottomSheetItems(
    playListOptions: List<PlayListOption>,
    onConfirmDeleteWatchHistory: () -> Unit = {},
    onPlayListSettings: () -> Unit = {},
    onConfirmDeletePlayList: () -> Unit = {},
): List<BottomSheetItem> {
    val result = mutableListOf<BottomSheetItem>()
    playListOptions.forEach { playListOption ->
        when (playListOption) {
            PlayListOption.DeleteWatchHistory -> result.add(
                BottomSheetItem(
                    imageResource = R.drawable.ic_trash,
                    text = stringResource(R.string.delete_watch_history),
                    action = onConfirmDeleteWatchHistory
                )
            )

            PlayListOption.PlayListSettings -> result.add(
                BottomSheetItem(
                    imageResource = R.drawable.ic_settings,
                    text = stringResource(R.string.playlist_settings),
                    action = onPlayListSettings
                )
            )

            PlayListOption.DeletePlayList -> result.add(
                BottomSheetItem(
                    imageResource = R.drawable.ic_trash,
                    text = stringResource(R.string.delete_playlist),
                    action = onConfirmDeletePlayList
                )
            )
        }
    }
    return result
}