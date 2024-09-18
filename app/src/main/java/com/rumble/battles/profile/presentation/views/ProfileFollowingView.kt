package com.rumble.battles.profile.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.commonViews.FollowingContentView
import com.rumble.theme.paddingLarge
import com.rumble.theme.radiusMedium

@Composable
fun ProfileFollowingView(
    modifier: Modifier = Modifier,
    rumbles: Int = 0,
    followers: Int = 0,
    following: Int = 0
) {

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radiusMedium))
            .background(MaterialTheme.colors.onSurface)
    ) {
        FollowingContentView(
            modifier = modifier
                .padding(
                    top = paddingLarge,
                    bottom = paddingLarge
                ),
            likes = rumbles,
            followers = followers,
            following = following
        )
    }
}

@Composable
@Preview
fun PreviewProfileFollowingView() {

    ProfileFollowingView(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        rumbles = 100,
        followers = 1222,
        following = 204
    )
}