package com.rumble.videoplayer.presentation.internal.controlViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.rumble.theme.RumbleTvTypography.h3Tv
import com.rumble.theme.brandedPlayerBackground
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXXSmall
import com.rumble.videoplayer.R
import com.rumble.videoplayer.player.RumbleVideo
import com.rumble.videoplayer.player.internal.notification.PlayListType
import com.rumble.videoplayer.player.internal.notification.RumblePlayList

@Composable
internal fun PlayListView(
    modifier: Modifier = Modifier,
    playList: RumblePlayList,
    expended: Boolean,
    currentVideoId: Long,
    currentSelectedIndex: Int,
    onSaveFocusSelection: (Int) -> Unit,
    onVideoSelected: (Long) -> Unit,
    videoCardComposable: @Composable (video: RumbleVideo, isPlaying: Boolean, onFocused: () -> Unit, onSelection: () -> Unit) -> Unit,
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val scrollState = rememberLazyListState()

    LaunchedEffect(currentSelectedIndex) {
        selectedIndex = currentSelectedIndex
        scrollState.scrollToItem(selectedIndex)
    }

    LaunchedEffect(expended) {
        if (expended.not()) {
            onSaveFocusSelection(selectedIndex)
        } else if (selectedIndex > 1) {
            scrollState.scrollToItem(selectedIndex)
        }
    }

    Column(
        modifier = modifier
            .background(brandedPlayerBackground.copy(0.2f))
            .padding(top = paddingSmall)
    ) {
        if (expended) {
            Row(
                modifier = Modifier
                    .padding(bottom = paddingSmall, end = paddingMedium, start = paddingLarge)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (playList.type == PlayListType.PlayList) {
                    Icon(
                        modifier = Modifier.padding(end = paddingSmall),
                        painter = painterResource(id = R.drawable.ic_playlist),
                        contentDescription = playList.title,
                        tint = enforcedWhite
                    )
                }

                Text(
                    text = playList.title,
                    color = enforcedWhite,
                    style = h3Tv
                )
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(paddingXXSmall),
            contentPadding = PaddingValues(horizontal = paddingMedium),
            state = scrollState,
        ) {
            itemsIndexed(playList.videoList) { index, item ->
                videoCardComposable(
                    item,
                    currentVideoId == item.videoId,
                    onFocused = { selectedIndex = index }
                ) { onVideoSelected(item.videoId) }
            }
        }
    }
}

