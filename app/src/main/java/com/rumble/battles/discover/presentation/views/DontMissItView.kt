package com.rumble.battles.discover.presentation.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import com.rumble.battles.DoNotMissItContentTag
import com.rumble.battles.DoNotMissItErrorTag
import com.rumble.battles.DoNotMissItLoadingTag
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.feed.presentation.views.VideoView
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.theme.defaultDiscoverContentLoadingHeight
import com.rumble.theme.paddingLarge
import com.rumble.videoplayer.player.RumblePlayer

@Composable
fun DontMissItView(
    modifier: Modifier,
    contentHandler: ContentHandler,
    doNotMissItVideo: VideoEntity?,
    loading: Boolean,
    error: Boolean,
    soundOn: Boolean,
    rumblePlayer: RumblePlayer? = null,
    onSoundClick: () -> Unit,
    onChannelClick: (channelId: String) -> Unit,
    onVideoClick: () -> Unit,
    onLike: (VideoEntity) -> Unit,
    onDislike: (VideoEntity) -> Unit,
    onRefresh: () -> Unit,
    onImpression: (VideoEntity) -> Unit,
    onInvisible: () -> Unit
) {
    DisposableEffect(Unit) {
        onDispose { onInvisible() }
    }

    Column(modifier = modifier) {
        if (loading) {
            LoadingView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(defaultDiscoverContentLoadingHeight)
                    .semantics { testTag = DoNotMissItLoadingTag }
            )
        } else if (error) {
            ErrorView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(defaultDiscoverContentLoadingHeight)
                    .semantics { testTag = DoNotMissItErrorTag },
                onRetry = onRefresh
            )
        } else if (doNotMissItVideo != null) {
            VideoView(
                modifier = Modifier
                    .semantics { testTag = DoNotMissItContentTag }
                    .fillMaxWidth(),
                soundOn = soundOn,
                videoEntity = doNotMissItVideo,
                rumblePlayer = rumblePlayer,
                onSoundClick = onSoundClick,
                onChannelClick = { onChannelClick(doNotMissItVideo.channelId) },
                onMoreClick = { contentHandler.onMoreVideoOptionsClicked(it) },
                onImpression = onImpression,
                onClick = { onVideoClick() },
                featured = true,
                isPremiumUser = contentHandler.isPremiumUser(),
            )
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = paddingLarge),
            color = MaterialTheme.colors.secondaryVariant
        )
    }
}