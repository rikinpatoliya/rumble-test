package com.rumble.battles.camera.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.rumble.battles.R
import com.rumble.battles.UploadCameraTag
import com.rumble.battles.camera.presentation.views.CameraPreview
import com.rumble.battles.commonViews.snackbar.RumbleSnackbarHost
import com.rumble.battles.commonViews.snackbar.showRumbleSnackbar

@SuppressLint("SourceLockedOrientationActivity")
@Composable
fun CameraModeScreen(
    cameraHandler: CameraHandler,
    onPreviewRecording: (uri: String) -> Unit,
    onClose: () -> Unit
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
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

    LaunchedEffect(cameraHandler.cameraHandlerEventFlow) {
        cameraHandler.cameraHandlerEventFlow.collect { event ->
            when (event) {
                CameraHandlerVmEvent.RecordingError -> {
                    snackBarHostState.showRumbleSnackbar(
                        message = context.getString(R.string.recording_error_retry)
                    )
                }
                CameraHandlerVmEvent.RecordingStartError -> {
                    snackBarHostState.showRumbleSnackbar(
                        message = context.getString(R.string.recording_start_error_retry)
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
    RumbleSnackbarHost(snackBarHostState)
}