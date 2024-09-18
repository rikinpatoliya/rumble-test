package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.domain.feed.domain.domainmodel.video.VideoStatus
import com.rumble.domain.settings.domain.domainmodel.ListToggleViewStyle
import com.rumble.theme.RumbleTypography.tinyBodySemiBold
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusXSmall
import com.rumble.utils.extension.getDateString
import com.rumble.utils.extension.getTimeString
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
                .clip(RoundedCornerShape(radiusXSmall))
                .background(enforcedDarkmo)
        ) {
            Row(
                modifier = Modifier.padding(
                    vertical = paddingXXXSmall,
                    horizontal = paddingXSmall
                )
            ) {
                StateTagImageView(
                    videoStatus = videoStatus,
                    listToggleViewStyle = ListToggleViewStyle.GRID
                )

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

@Composable
private fun TagText(
    modifier: Modifier = Modifier,
    videoStatus: VideoStatus,
    scheduled: LocalDateTime?,
    watching: Long
) {
    val text = when (videoStatus) {
        VideoStatus.SCHEDULED -> scheduled?.let {
            "${
                it.getDateString().uppercase()
            }, ${it.getTimeString()}"
        } ?: ""

        VideoStatus.UPCOMING -> stringResource(id = R.string.upcoming).uppercase()
        VideoStatus.STARTING -> stringResource(id = R.string.starting).uppercase()
        VideoStatus.LIVE -> watching.shortString(withDecimal = true)
        else -> stringResource(id = R.string.streamed).uppercase()
    }

    Text(
        modifier = modifier,
        text = text,
        style = tinyBodySemiBold,
        color = enforcedWhite
    )
}