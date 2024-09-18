package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.domain.feed.domain.domainmodel.video.VideoStatus
import com.rumble.domain.settings.domain.domainmodel.ListToggleViewStyle
import com.rumble.theme.RumbleTypography.tinyBodySemiBold8dp
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.paddingXXXXSmall
import com.rumble.theme.radiusXXSmall
import com.rumble.utils.extension.getDateShortMonthString
import com.rumble.utils.extension.parsedTime
import com.rumble.utils.extension.shortString
import java.time.LocalDateTime

@Composable
fun StateTagCompactView(
    modifier: Modifier = Modifier,
    videoStatus: VideoStatus,
    scheduled: LocalDateTime?,
    watching: Long,
    duration: Long
) {

    Row(modifier = modifier) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(radiusXXSmall))
                .background(enforcedDarkmo)
        ) {
            Row(
                modifier = Modifier.padding(
                    vertical = paddingXXXXSmall,
                    horizontal = paddingXXSmall
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StateTagImageView(
                    videoStatus = videoStatus,
                    listToggleViewStyle = ListToggleViewStyle.LIST
                )

                TagText(
                    modifier = Modifier.padding(start = paddingXXXSmall),
                    videoStatus = videoStatus,
                    scheduled = scheduled,
                    watching = watching,
                    duration = duration
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
    watching: Long,
    duration: Long
) {

    val text = when (videoStatus) {
        VideoStatus.SCHEDULED -> scheduled?.let {
            "${
                it.getDateShortMonthString().uppercase()
            } ${it.dayOfMonth}"
        } ?: ""

        VideoStatus.UPCOMING -> stringResource(id = R.string.upcoming).uppercase()
        VideoStatus.STARTING -> stringResource(id = R.string.starting).uppercase()
        VideoStatus.LIVE -> watching.shortString()
        else -> if (duration > 0L) duration.parsedTime() else ""
    }
    if (text.isNotEmpty())
        Text(
            modifier = modifier,
            text = text,
            style = tinyBodySemiBold8dp,
            color = enforcedWhite
        )
}