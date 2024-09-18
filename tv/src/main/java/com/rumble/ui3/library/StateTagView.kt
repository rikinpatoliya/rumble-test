package com.rumble.ui3.library


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.rumble.R
import com.rumble.domain.feed.domain.domainmodel.video.VideoStatus
import com.rumble.theme.RumbleTypography.h6Heavy
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusXXXSmall
import com.rumble.utils.extension.getMediumDateTimeString
import com.rumble.utils.extension.shortString
import java.time.LocalDateTime

@Composable
fun StateTagView(
    modifier: Modifier = Modifier,
    videoStatus: VideoStatus,
    scheduled: LocalDateTime?,
    watching: Long,
) {

    Row(modifier = modifier) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(radiusXXXSmall))
                .background(enforcedDarkmo)
        ) {
            Row(
                modifier = Modifier.padding(
                    vertical = paddingXXXSmall,
                    horizontal = paddingXSmall
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TagImage(videoStatus = videoStatus)

                TagText(
                    modifier = Modifier.padding(start = paddingXXXSmall),
                    videoStatus = videoStatus,
                    scheduled = scheduled,
                    watching = watching
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun TagText(
    modifier: Modifier = Modifier,
    videoStatus: VideoStatus,
    scheduled: LocalDateTime?,
    watching: Long,
) {
    val text = when (videoStatus) {
        VideoStatus.SCHEDULED -> scheduled?.getMediumDateTimeString()
            ?: ""

        VideoStatus.UPCOMING -> stringResource(id = R.string.video_card_upcoming_label).uppercase()
        VideoStatus.STARTING -> stringResource(id = R.string.video_card_live_starting_label).uppercase()
        VideoStatus.LIVE -> watching.shortString(withDecimal = true)
        else -> stringResource(id = R.string.video_card_live_stream_ended_label).uppercase()
    }

    Text(
        modifier = modifier,
        text = text,
        style = h6Heavy,
        color = enforcedWhite
    )
}

@Composable
private fun TagImage(videoStatus: VideoStatus) {

    when (videoStatus) {
        VideoStatus.SCHEDULED, VideoStatus.UPCOMING ->
            Image(
                painter = painterResource(id = R.drawable.v3_ic_clock),
                contentDescription = stringResource(id = R.string.video_card_upcoming_label),
            )

        VideoStatus.STARTING ->
            Image(
                painter = painterResource(id = R.drawable.v3_ic_clock),
                contentDescription = stringResource(id = R.string.video_card_live_starting_label),
            )

        VideoStatus.LIVE ->
            Image(
                painter = painterResource(id = R.drawable.v3_ic_views_24_white),
                contentDescription = stringResource(id = R.string.live),
            )

        else ->
            Image(
                painter = painterResource(id = R.drawable.v3_ic_arrow_s_turn),
                contentDescription = stringResource(id = R.string.video_card_live_stream_ended_label),
            )
    }
}
