package com.rumble.battles.camera.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.rumble.battles.R
import com.rumble.battles.UploadCameraTag
import com.rumble.battles.camera.presentation.views.CameraPreview
import com.rumble.battles.content.presentation.ContentHandler

@SuppressLint("SourceLockedOrientationActivity")
@Composable
fun CameraModeScreen(
    cameraHandler: CameraHandler,
    contentHandler: ContentHandler,
    onPreviewRecording: (uri: String) -> Unit,
    onClose: () -> Unit
) {
    val localView = LocalView.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val systemUiController = rememberSystemUiController()
    systemUiController.isSystemBarsVisible = false

    LaunchedEffect(lifecycleOwner) {
        localView.keepScreenOn = true
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            localView.keepScreenOn = false
            systemUiController.isSystemBarsVisible = true
        }
    }

    LaunchedEffect(Unit) {
        cameraHandler.cameraHandlerEventFlow.collect { event ->
            when (event) {
                CameraHandlerVmEvent.RecordingError -> {
                    contentHandler.onShowSnackBar(
                        messageId = R.string.recording_error_retry,
                        withPadding = false
                    )
                }

                CameraHandlerVmEvent.RecordingStartError -> {
                    contentHandler.onShowSnackBar(
                        messageId = R.string.recording_start_error_retry,
                        withPadding = false
                    )
                }

                else -> {}
            }
        }
    }

    Box(
        modifier = Modifier
            .testTag(UploadCameraTag)
            .fillMaxSize()
    ) {
        CameraPreview(
            cameraHandler = cameraHandler,
            onPreviewRecording = onPreviewRecording
        ) {
            onClose()
        }
    }
}