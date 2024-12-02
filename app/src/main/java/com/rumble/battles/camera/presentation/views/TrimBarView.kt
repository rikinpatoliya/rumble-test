package com.rumble.battles.camera.presentation.views

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderPositions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import com.rumble.battles.R
import com.rumble.battles.camera.presentation.CameraHandler
import com.rumble.battles.camera.presentation.TrimBarData
import com.rumble.battles.commonViews.RumbleProgressIndicator
import com.rumble.theme.enforcedBlack
import com.rumble.theme.enforcedCloud
import com.rumble.theme.enforcedFiord
import com.rumble.theme.enforcedWhite
import com.rumble.theme.imageMini
import com.rumble.theme.imageXXSmall
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.paddingXXXXSmall
import com.rumble.theme.radiusLarge
import com.rumble.theme.radiusMedium
import com.rumble.theme.radiusSmall
import com.rumble.theme.rumbleGreen
import com.rumble.theme.videoTrimmerHeight
import com.rumble.theme.videoTrimmerPlayHeadHeight
import com.rumble.theme.videoTrimmerPreviewHorizontalThumbnailWidth
import com.rumble.theme.videoTrimmerPreviewVerticalThumbnailWidth

@Composable
fun TrimBarView(
    modifier: Modifier = Modifier,
    exoPlayer: ExoPlayer,
    cameraHandler: CameraHandler,
    trimBarData: TrimBarData,
    trimThumbnails: List<Bitmap>,
    duration: Long,
    onCurrentPositionChange: (value: Float) -> Unit,
) {
    val startInteractionSource = remember { MutableInteractionSource() }
    val endInteractionSource = remember { MutableInteractionSource() }
    var sliderDragInProgress by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        if (trimThumbnails.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(videoTrimmerPlayHeadHeight)
            ) {
                RumbleProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(videoTrimmerHeight)
                    .clip(RoundedCornerShape(radiusMedium))
                    .background(color = enforcedFiord)
                    .align(Alignment.Center),
            ) {
                LazyRow(
                    modifier = Modifier.padding(
                        start = paddingMedium,
                        end = paddingMedium,
                        top = paddingXXXXSmall,
                        bottom = paddingXXXXSmall,
                    )
                ) {
                    items(trimThumbnails) {
                        TrimThumbnailView(
                            bitmap = it,
                        )
                    }
                }
                if (sliderDragInProgress.not())
                    PreviewPlaybackSlider(//TODO: work on better solution for UI
                        duration = duration, trimBarData = trimBarData
                    )
                RangeSliderWithCustomComponents(
                    duration = duration,
                    currentPosition = trimBarData.currentPosition,
                    sliderPosition = trimBarData.sliderPosition,
                    startInteractionSource = startInteractionSource,
                    endInteractionSource = endInteractionSource,
                    onSliderPositionChange = {
                        exoPlayer.pause()
                        cameraHandler.updateSliderPosition(it)
                    },
                    onSliderPositionChangeFinished = {
                        cameraHandler.updateSlidingState(false)
                        if (trimBarData.loopVideo)
                            exoPlayer.play()
                    },
                    onCurrentPositionChange = {
                        onCurrentPositionChange(it)
                    },
                    onSliderDragInProgress = {
                        sliderDragInProgress = it
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RangeSliderWithCustomComponents(
    duration: Long,
    currentPosition: Float,
    sliderPosition: ClosedFloatingPointRange<Float>,
    startInteractionSource: MutableInteractionSource,
    endInteractionSource: MutableInteractionSource,
    onSliderPositionChange: (closedFloatingPointRange: ClosedFloatingPointRange<Float>) -> Unit,
    onSliderPositionChangeFinished: (closedFloatingPointRange: ClosedFloatingPointRange<Float>) -> Unit,
    onCurrentPositionChange: (value: Float) -> Unit,
    onSliderDragInProgress: (Boolean) -> Unit = {}
) {
    var dragCurrentPosition by rememberSaveable { mutableFloatStateOf(currentPosition) }

    androidx.compose.material3.RangeSlider(
        value = sliderPosition,
        onValueChange = {
            if (it.start - sliderPosition.start > duration*0.05
                && dragCurrentPosition in sliderPosition) {
                dragCurrentPosition = it.start
                onCurrentPositionChange(it.start)
            } else if (sliderPosition.endInclusive - it.endInclusive > duration*0.05
                && dragCurrentPosition in sliderPosition) {
                dragCurrentPosition = it.endInclusive
                onCurrentPositionChange(it.endInclusive)
            } else {
                onSliderDragInProgress(true)
                if (it.start > dragCurrentPosition) {
                    dragCurrentPosition = it.start
                    onCurrentPositionChange(dragCurrentPosition)
                } else if (it.endInclusive < dragCurrentPosition) {
                    dragCurrentPosition = it.endInclusive
                    onCurrentPositionChange(dragCurrentPosition)
                }
                onSliderPositionChange(it)
            }
        },
        modifier = Modifier
            .fillMaxWidth(),
        valueRange = 0f..duration.toFloat(),
        onValueChangeFinished = {
            onSliderDragInProgress(false)
            onSliderPositionChangeFinished(sliderPosition)
            if (dragCurrentPosition in sliderPosition)
                onCurrentPositionChange(dragCurrentPosition)
        },
        startInteractionSource = startInteractionSource,
        endInteractionSource = endInteractionSource,
        startThumb = {
            TrimmerEdge(start = true)
        },
        endThumb = {
            TrimmerEdge(start = false)
        },
        track = { sliderPositions ->
            Track(
                sliderPositions = sliderPositions,
            )
        }
    )
}

@Composable
fun TrimThumbnailView(
    modifier: Modifier = Modifier,
    bitmap: Bitmap,
) {
    Box(
        modifier = modifier
            .width(if (bitmap.height > bitmap.width) videoTrimmerPreviewVerticalThumbnailWidth else videoTrimmerPreviewHorizontalThumbnailWidth)
            .height(videoTrimmerHeight)
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = bitmap,
            contentDescription = "",
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
fun Track(
    sliderPositions: SliderPositions,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier
            .fillMaxWidth()
            .height(videoTrimmerHeight)
    ) {
        val isRtl = layoutDirection == LayoutDirection.Rtl
        val sliderLeft = Offset(0f.plus(paddingXXSmall.toPx()), center.y)
        val sliderRight = Offset(size.width.minus(paddingXXSmall.toPx()), center.y)
        val sliderStart = if (isRtl) sliderRight else sliderLeft
        val sliderEnd = if (isRtl) sliderLeft else sliderRight
        val trackStrokeWidth = (videoTrimmerHeight - paddingXXXSmall).toPx()
        val lineStrokeWidth = paddingXXXSmall.toPx()
        val sliderValueEnd = Offset(
            sliderStart.x +
                    (sliderEnd.x - sliderStart.x) * sliderPositions.activeRange.endInclusive,
            center.y
        )

        val sliderValueStart = Offset(
            sliderStart.x +
                    (sliderEnd.x - sliderStart.x) * sliderPositions.activeRange.start,
            center.y
        )
        val topBorderValueStart = Offset(
            sliderStart.x +
                    (sliderEnd.x - sliderStart.x) * sliderPositions.activeRange.start,
            0f
        )
        val topBorderValueEnd = Offset(
            sliderStart.x +
                    (sliderEnd.x - sliderStart.x) * sliderPositions.activeRange.endInclusive,
            0f
        )
        val bottomBorderValueStart = Offset(
            sliderStart.x +
                    (sliderEnd.x - sliderStart.x) * sliderPositions.activeRange.start,
            size.height
        )
        val bottomBorderValueEnd = Offset(
            sliderStart.x +
                    (sliderEnd.x - sliderStart.x) * sliderPositions.activeRange.endInclusive,
            size.height
        )

        drawLine(
            enforcedBlack.copy(alpha = 0.6F),
            sliderStart,
            sliderValueStart,
            trackStrokeWidth,
            StrokeCap.Butt
        )
        drawLine(
            enforcedBlack.copy(alpha = 0.6F),
            sliderValueEnd,
            sliderEnd,
            trackStrokeWidth,
            StrokeCap.Butt
        )
        drawLine(
            enforcedWhite,
            topBorderValueStart,
            topBorderValueEnd,
            lineStrokeWidth,
            StrokeCap.Butt
        )
        drawLine(
            enforcedWhite,
            bottomBorderValueStart,
            bottomBorderValueEnd,
            lineStrokeWidth,
            StrokeCap.Butt
        )
    }
}

@Composable
fun TrimmerEdge(start: Boolean) {
    val shape = if (start) RoundedCornerShape(
        topStart = radiusLarge,
        bottomStart = radiusLarge
    ) else RoundedCornerShape(topEnd = radiusLarge, bottomEnd = radiusLarge)
    Box(
        modifier = Modifier
            .height(videoTrimmerHeight)
            .width(imageXXSmall)
            .clip(shape = shape)
            .background(color = enforcedWhite)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_union),
            contentDescription = "",
            modifier = Modifier.align(Alignment.Center),
            tint = enforcedCloud
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewPlaybackSlider(
    duration: Long,
    trimBarData: TrimBarData,
) {
    androidx.compose.material3.Slider(
        modifier = Modifier.fillMaxSize(),
        value = trimBarData.currentPosition.coerceIn(trimBarData.sliderPosition),
        onValueChange = {},
        onValueChangeFinished = {},
        valueRange = 0f..duration.toFloat(),
        colors = SliderDefaults.colors(
            thumbColor = Color.Transparent,
            activeTrackColor = Color.Transparent,
            inactiveTrackColor = Color.Transparent
        ),
        thumb = {
            Box(
                modifier = Modifier
                    .width(imageMini)
                    .fillMaxHeight()
                    .clip(
                        RoundedCornerShape(radiusSmall)
                    )
                    .background(color = rumbleGreen)
            )
        }
    )
}