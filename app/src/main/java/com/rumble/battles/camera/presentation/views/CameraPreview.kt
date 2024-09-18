package com.rumble.battles.camera.presentation.views

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.Surface
import android.view.WindowInsetsController
import androidx.activity.compose.BackHandler
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.util.Consumer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.rumble.battles.R
import com.rumble.battles.camera.presentation.CameraHandler
import com.rumble.battles.camera.presentation.CameraHandlerVmEvent
import com.rumble.battles.commonViews.IsTablet
import com.rumble.theme.RumbleTypography
import com.rumble.theme.RumbleTypography.h6Heavy
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedWhite
import com.rumble.theme.fierceRed
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXLarge
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusXMedium
import com.rumble.theme.radiusXXXMedium
import com.rumble.theme.recordTimerTimeBoxSize
import com.rumble.utils.RumbleConstants.RUMBLE_VIDEO_EXTENSION
import com.rumble.utils.RumbleConstants.RUMBLE_VIDEO_MEDIAFILE_PREFIX
import com.rumble.utils.RumbleConstants.RUMBLE_VIDEO_MEDIASTORE_FILE_LOCATION
import com.rumble.utils.RumbleConstants.RUMBLE_VIDEO_MYME_TYPE
import com.rumble.utils.RumbleConstants.VIDEO_FILE_NAME_FORMAT
import com.rumble.utils.extension.conditional
import com.rumble.utils.extension.getCameraProvider
import com.rumble.utils.extension.toRotationFloat
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor

@SuppressLint("InlinedApi")
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    cameraHandler: CameraHandler,
    onPreviewRecording: (uri: String) -> Unit,
    onClose: () -> Unit = {},
) {
    val systemUiController = rememberSystemUiController()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView: PreviewView = remember { PreviewView(context) }
    val uiState by cameraHandler.cameraHandlerUiState.collectAsStateWithLifecycle()

    val videoCapture: MutableState<VideoCapture<Recorder>?> = remember { mutableStateOf(null) }

    BackHandler(uiState.recordingStarted) { /*Has to ignore when recording is in progress*/ }

    LaunchedEffect(uiState.rotation) {
        if (uiState.recordingStarted.not() && videoCapture.value?.targetRotation != uiState.rotation)
            videoCapture.value?.targetRotation = uiState.rotation
    }

    LaunchedEffect(previewView) {
        videoCapture.value = context.createVideoCaptureUseCase(
            lifecycleOwner = lifecycleOwner,
            cameraSelector = uiState.cameraSelector,
            flashEnabled = uiState.flashEnabled,
            previewView = previewView
        )
    }

    LaunchedEffect(Unit) {
        systemUiController.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        cameraHandler.cameraHandlerEventFlow.collect { event ->
            when (event) {
                is CameraHandlerVmEvent.PreviewRecording -> onPreviewRecording(event.uri)
                else -> {}
            }
        }
    }

    Box(modifier = modifier) {
        val isTablet = IsTablet()
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
        if (uiState.recordingStarted) {
            val recordTimerAlignment = when (uiState.rotation) {
                Surface.ROTATION_90 -> Alignment.TopEnd
                Surface.ROTATION_270 -> Alignment.BottomStart
                else -> Alignment.TopStart
            }
            Box(
                modifier = Modifier
                    .size(recordTimerTimeBoxSize)
                    .conditional(isTablet.not()) {
                       this.rotate(uiState.rotation.toRotationFloat())
                    }
                    .align(if (isTablet) Alignment.TopStart else recordTimerAlignment)
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = paddingMedium, top = paddingLarge)
                        .clip(RoundedCornerShape(radiusXXXMedium))
                        .background(color = fierceRed)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dot),
                        contentDescription = stringResource(id = R.string.record_time_description),
                        modifier = Modifier
                            .padding(start = paddingSmall)
                            .align(Alignment.CenterVertically),
                        tint = enforcedWhite
                    )
                    Text(
                        text = uiState.timerText,
                        modifier = Modifier.padding(
                            top = paddingXXSmall,
                            bottom = paddingXXSmall,
                            start = paddingXXSmall,
                            end = paddingSmall,
                        ),
                        color = enforcedWhite,
                        style = h6Heavy
                    )
                }
            }
        } else {
            val closeIconAlignment = when (uiState.rotation) {
                Surface.ROTATION_270 -> Alignment.TopStart
                else -> Alignment.TopEnd
            }
            IconButton(
                modifier = Modifier
                    .padding(start = paddingXSmall, end = paddingXSmall, top = paddingMedium)
                    .align(if (isTablet) Alignment.TopEnd else closeIconAlignment),
                onClick = onClose
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = stringResource(id = R.string.close),
                    tint = enforcedWhite
                )
            }
        }

        if (uiState.recordingStarted.not()) {
            val iconsAlignment = when (uiState.rotation) {
                Surface.ROTATION_90, Surface.ROTATION_270 -> Alignment.TopCenter
                else -> Alignment.CenterEnd
            }
            Column(
                modifier = Modifier
                    .conditional(isTablet.not()) {
                        this.rotate(uiState.rotation.toRotationFloat())
                    }
                    .padding(start = paddingMedium, end = paddingMedium)
                    .align(if (isTablet) Alignment.CenterEnd else iconsAlignment),
            ) {
                if (uiState.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                    CameraActionIcon(
                        iconId = if (uiState.flashEnabled) R.drawable.ic_flash_on else R.drawable.ic_flash_off,
                        textId = R.string.flash
                    ) {
                        val enable = uiState.flashEnabled.not()
                        cameraHandler.toggleFlash(enable)
                        lifecycleOwner.lifecycleScope.launch {
                            videoCapture.value = context.createVideoCaptureUseCase(
                                lifecycleOwner = lifecycleOwner,
                                cameraSelector = uiState.cameraSelector,
                                flashEnabled = enable,
                                previewView = previewView
                            )
                        }
                    }
                }
                CameraActionIcon(
                    modifier = Modifier
                        .padding(top = paddingLarge),
                    iconId = R.drawable.ic_flip,
                    textId = R.string.flip
                ) {
                    val cameraSelector =
                        if (uiState.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                        else CameraSelector.DEFAULT_BACK_CAMERA
                    cameraHandler.toggleCameraSelector(cameraSelector)
                    lifecycleOwner.lifecycleScope.launch {
                        videoCapture.value = context.createVideoCaptureUseCase(
                            lifecycleOwner = lifecycleOwner,
                            cameraSelector = cameraSelector,
                            flashEnabled = uiState.flashEnabled,
                            previewView = previewView
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .padding(bottom = paddingXLarge)
                .align(Alignment.BottomCenter),
        ) {
            CameraRecordIcon(recording = uiState.recordingStarted) {
                if (uiState.recordingStarted.not()) {
                    videoCapture.value?.let { videoCapture ->
                        val recording = startRecordingVideo(
                            context = context,
                            videoCapture = videoCapture,
                            executor = ContextCompat.getMainExecutor(context),
                        ) { event ->
                            if (event is VideoRecordEvent.Finalize
                                && cameraHandler.cameraHandlerUiState.value.recordingCancelled.not()
                            ) {
                                if (!event.hasError()) {
                                    val uri = event.outputResults.outputUri
                                    if (uri != Uri.EMPTY) {
                                        cameraHandler.stopRecording()
                                        cameraHandler.onPreviewRecording(uri, RUMBLE_VIDEO_EXTENSION)
                                    }
                                } else {
                                    cameraHandler.stopRecordingWithError(Throwable("Video capture ends with error: ${event.error}"))
                                }
                            } else if (event is VideoRecordEvent.Status) {
                                cameraHandler.updateDuration(event.recordingStats.recordedDurationNanos)
                            }
                        }
                        cameraHandler.startRecording(recording)
                    }
                } else {
                    cameraHandler.stopRecording()
                }
            }
        }
    }
}

@Composable
fun CameraRecordIcon(
    modifier: Modifier = Modifier,
    recording: Boolean = false,
    onClick: () -> Unit,
) {
    Image(
        modifier = modifier
            .clip(CircleShape)
            .background(color = enforcedWhite.copy(alpha = 0.05f))
            .conditional(recording.not()) {
                this.blur(radius = radiusXMedium)
            }
            .clickable { onClick() },
        painter = painterResource(id = if (recording) R.drawable.ic_record_stop else R.drawable.ic_record),
        contentDescription = stringResource(id = R.string.record_description)
    )
}

@Composable
fun CameraActionIcon(
    modifier: Modifier = Modifier,
    iconId: Int,
    textId: Int,
    onClick: () -> Unit
) {
    ConstraintLayout(
        modifier = modifier
    ) {
        val (icon, text) = createRefs()
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(enforcedDarkmo.copy(alpha = 0.2f))
                .clickable { onClick() }
                .constrainAs(icon) {
                    top.linkTo(parent.top)
                },
        ) {
            Icon(
                modifier = Modifier.padding(paddingSmall),
                painter = painterResource(id = iconId),
                contentDescription = stringResource(id = textId),
                tint = enforcedWhite
            )
        }
        Text(
            text = stringResource(id = textId),
            modifier = Modifier
                .padding(top = paddingXXXSmall)
                .constrainAs(text) {
                    top.linkTo(icon.bottom)
                    start.linkTo(icon.start)
                    end.linkTo(icon.end)
                },
            color = enforcedWhite,
            style = RumbleTypography.tinyBody
        )
    }
}

private fun startRecordingVideo(
    context: Context,
    videoCapture: VideoCapture<Recorder>,
    executor: Executor,
    consumer: Consumer<VideoRecordEvent>
): Recording? {
    val name = RUMBLE_VIDEO_MEDIAFILE_PREFIX +
            SimpleDateFormat(VIDEO_FILE_NAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis()) + "." + RUMBLE_VIDEO_EXTENSION
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, RUMBLE_VIDEO_MYME_TYPE)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(
                MediaStore.Video.Media.RELATIVE_PATH,
                RUMBLE_VIDEO_MEDIASTORE_FILE_LOCATION
            )
        }
    }
    val mediaStoreOutput = MediaStoreOutputOptions.Builder(
        context.contentResolver,
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    )
        .setContentValues(contentValues)
        .build()


    return try {
        videoCapture.output
            .prepareRecording(context, mediaStoreOutput)
            .apply {
                if (PermissionChecker.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    ) ==
                    PermissionChecker.PERMISSION_GRANTED
                ) {
                    withAudioEnabled()
                }
            }
            .start(executor, consumer)
    } catch (e: IllegalStateException) {
        null
    }
}

private suspend fun Context.createVideoCaptureUseCase(
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector,
    flashEnabled: Boolean,
    previewView: PreviewView
): VideoCapture<Recorder> {
    val preview = Preview.Builder()
        .build()
        .apply { setSurfaceProvider(previewView.surfaceProvider) }

    val recorder = Recorder.Builder()
        .setExecutor(ContextCompat.getMainExecutor(this))
        .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
        .build()
    val videoCapture = VideoCapture.withOutput(recorder)

    val cameraProvider = getCameraProvider()
    cameraProvider.unbindAll()
    if (lifecycleOwner.lifecycle.currentState != Lifecycle.State.DESTROYED) {
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            videoCapture
        ).apply {
            if (this.cameraInfo.hasFlashUnit())
                this.cameraControl.enableTorch(flashEnabled)
        }
    }

    return videoCapture
}