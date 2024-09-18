package com.rumble.battles.commonViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.domain.uploadmanager.dto.VideoUploadsIndicatorStatus
import com.rumble.theme.enforcedWhite
import com.rumble.theme.fierceRed
import com.rumble.theme.imageXXSmall
import com.rumble.theme.profileItemIconContentPadding
import com.rumble.theme.rumbleGreen

@Composable
fun VideoUploadingIndicatorView(
    videoUploadsIndicatorStatus: VideoUploadsIndicatorStatus,
    progress: Float = 0F
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(getUploadingIconBackgroundColor(videoUploadsIndicatorStatus)),
        contentAlignment = Alignment.Center
    ) {
        if (videoUploadsIndicatorStatus == VideoUploadsIndicatorStatus.Processing)
            CircularProgressIndicator(
                progress = progress,
                color = rumbleGreen,
            )
        Icon(
            modifier = Modifier
                .padding(profileItemIconContentPadding)
                .size(imageXXSmall),
            painter = painterResource(id = getUploadingIconDrawable(videoUploadsIndicatorStatus)),
            contentDescription = stringResource(id = R.string.uploads),
            tint = getUploadingIconTintColor(videoUploadsIndicatorStatus)
        )
    }
}

@Composable
private fun getUploadingIconDrawable(videoUploadsIndicatorStatus: VideoUploadsIndicatorStatus): Int {
    return when (videoUploadsIndicatorStatus) {
        VideoUploadsIndicatorStatus.Error -> R.drawable.ic_alert_triangle
        VideoUploadsIndicatorStatus.Finished -> R.drawable.ic_check
        else -> R.drawable.ic_upload_cloud
    }
}

@Composable
private fun getUploadingIconBackgroundColor(videoUploadsIndicatorStatus: VideoUploadsIndicatorStatus): Color {
    return when (videoUploadsIndicatorStatus) {
        VideoUploadsIndicatorStatus.Error -> fierceRed
        VideoUploadsIndicatorStatus.Finished -> rumbleGreen
        else -> MaterialTheme.colors.onPrimary
    }
}

@Composable
private fun getUploadingIconTintColor(videoUploadsIndicatorStatus: VideoUploadsIndicatorStatus): Color {
    return when (videoUploadsIndicatorStatus) {
        VideoUploadsIndicatorStatus.Error, VideoUploadsIndicatorStatus.Finished -> enforcedWhite
        else -> MaterialTheme.colors.secondary
    }
}

@Composable
@Preview
private fun PreviewVideoUploadingIndicatorViewError() {
    VideoUploadingIndicatorView(videoUploadsIndicatorStatus = VideoUploadsIndicatorStatus.Error)
}

@Composable
@Preview
private fun PreviewVideoUploadingIndicatorViewFinished() {
    VideoUploadingIndicatorView(videoUploadsIndicatorStatus = VideoUploadsIndicatorStatus.Finished)
}

@Composable
@Preview
private fun PreviewVideoUploadingIndicatorViewNone() {
    VideoUploadingIndicatorView(videoUploadsIndicatorStatus = VideoUploadsIndicatorStatus.None)
}

@Composable
@Preview
private fun PreviewVideoUploadingIndicatorViewProcessing() {
    VideoUploadingIndicatorView(
        videoUploadsIndicatorStatus = VideoUploadsIndicatorStatus.Processing,
        progress = 1F
    )
}

@Composable
@Preview
private fun PreviewVideoUploadingIndicatorViewProcessingDefault() {
    VideoUploadingIndicatorView(
        videoUploadsIndicatorStatus = VideoUploadsIndicatorStatus.Processing,
        progress = 0.01F
    )
}
