package com.rumble.battles.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.rumble.battles.R
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.common.domain.usecase.AnnotatedStringWithActionsList
import com.rumble.domain.common.domain.usecase.AnnotatedTextAction
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoStatus
import com.rumble.domain.settings.domain.domainmodel.ListToggleViewStyle
import com.rumble.theme.wokeGreen
import com.rumble.utils.RumbleConstants
import com.rumble.utils.extension.agoString
import com.rumble.utils.extension.getTimeString
import com.rumble.utils.getRumbleUrlAnnotations
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

fun parseTextWithUrls(
    text: String,
    color: Color,
    onClick: (String) -> Unit,
): AnnotatedStringWithActionsList {
    val actionList = mutableListOf<AnnotatedTextAction>()
    val annotationsList = getRumbleUrlAnnotations(
        text,
        SpanStyle(
            color = wokeGreen,
            fontWeight = FontWeight.W700
        )
    )

    return try {
        actionList.add(AnnotatedTextAction(RumbleConstants.TAG_URL) { uri ->
            onClick(uri)
        })

        val annotatedString = buildAnnotatedString {
            append(text)
            addStyle(SpanStyle(color = color), 0, text.length)
            annotationsList.forEach {
                addStringAnnotation(
                    tag = RumbleConstants.TAG_URL,
                    annotation = it.url,
                    start = it.start,
                    end = it.end
                )
                it.spanStyle?.let { style -> addStyle(style, it.start, it.end) }
            }
        }
        AnnotatedStringWithActionsList(annotatedString, actionList)
    } catch (e: Exception) {
        AnnotatedStringWithActionsList(AnnotatedString(""), emptyList())
    }
}

@Composable
fun getStringTitleByStatus(
    videoEntity: VideoEntity,
    listToggleViewStyle: ListToggleViewStyle
): String {
    return when (videoEntity.videoStatus) {
        VideoStatus.UPLOADED -> videoEntity.uploadDate.agoString(LocalContext.current)
        VideoStatus.STREAMED -> getRelativeTimeString(videoEntity)
        VideoStatus.STARTING -> stringResource(id = R.string.starting)
        VideoStatus.UPCOMING -> stringResource(id = R.string.upcoming)
        VideoStatus.LIVE -> "${stringResource(id = R.string.started)} ${
            getRelativeTimeString(
                videoEntity
            )
        }"

        else -> {
            val liveTime = videoEntity.liveDateTime
            if (liveTime != null && liveTime.isAfter(LocalDateTime.now()))
                getScheduledTimeTitle(liveTime, listToggleViewStyle)
            else
                "${stringResource(id = R.string.started)} ${
                    getRelativeTimeString(
                        videoEntity
                    )
                }"
        }
    }
}

@Composable
fun getNotificationIcon(channelDetailsEntity: ChannelDetailsEntity) =
    if (channelDetailsEntity.pushNotificationsEnabled)
        R.drawable.ic_notifications_filled
    else if (channelDetailsEntity.emailNotificationsEnabled)
        R.drawable.ic_notifications
    else
        R.drawable.ic_notifications_off

fun buildDelimiterHighlightedAnnotatedString(
    input: String,
    delimiter: String,
    regularStyle: SpanStyle,
    highlightedStyle: SpanStyle,
): AnnotatedString =
    buildAnnotatedString {
        if (delimiter.isNotBlank()) {
            delimiter.trim().lowercase().let { searchTerm ->
                val startIndex = input.lowercase().indexOf(searchTerm)
                if (startIndex != -1) {
                    val endIndex = startIndex + searchTerm.length
                    append(input.substring(0, startIndex))
                    withStyle(style = highlightedStyle) {
                        append(input.substring(startIndex, endIndex))
                    }
                    withStyle(style = regularStyle) {
                        append(input.substring(endIndex))
                    }
                } else {
                    withStyle(style = regularStyle) {
                        append(input)
                    }
                }
            }
        } else {
            withStyle(style = regularStyle) {
                append(input)
            }
        }
    }

@Composable
private fun getScheduledTimeTitle(
    liveTime: LocalDateTime,
    listToggleViewStyle: ListToggleViewStyle
): String {
    val prefix =
        if (listToggleViewStyle == ListToggleViewStyle.GRID) "${stringResource(id = R.string.scheduled_for)} " else ""
    return "$prefix${
        liveTime.format(
            DateTimeFormatter.ofLocalizedDate(
                FormatStyle.LONG
            )
        )
    } ${
        stringResource(id = R.string.at)
    } ${liveTime.getTimeString(false).lowercase()}"
}


@Composable
private fun getRelativeTimeString(videoEntity: VideoEntity) =
    (videoEntity.liveStreamedOn ?: videoEntity.liveDateTime ?: videoEntity.uploadDate).agoString(
        LocalContext.current
    )
