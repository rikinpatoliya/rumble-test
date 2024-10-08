package com.rumble.battles.camera.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.database.ContentObserver
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.rumble.battles.R
import com.rumble.battles.UploadGalleryTag
import com.rumble.battles.camera.presentation.views.CameraRecordIcon
import com.rumble.battles.commonViews.EmptyView
import com.rumble.battles.commonViews.EmptyViewAction
import com.rumble.battles.commonViews.dialogs.DialogActionItem
import com.rumble.battles.commonViews.dialogs.DialogActionType
import com.rumble.battles.commonViews.dialogs.RumbleAlertDialog
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.login.presentation.AuthHandler
import com.rumble.battles.login.presentation.AuthPlaceholderScreen
import com.rumble.domain.camera.GalleryVideoEntity
import com.rumble.theme.RumbleTypography.h6Heavy
import com.rumble.theme.RumbleTypography.tinyBodyExtraBold
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedFiord
import com.rumble.theme.enforcedWhite
import com.rumble.theme.imageXMedium
import com.rumble.theme.imageXXLarge
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.radiusMedium
import com.rumble.theme.radiusXMedium
import com.rumble.theme.radiusXXXMedium
import com.rumble.theme.videoPreviewCompactHeight
import com.rumble.theme.videoPreviewCompactWidth
import com.rumble.theme.videoPreviewHeight
import com.rumble.theme.videoPreviewWidth
import com.rumble.utils.RumbleConstants
import com.rumble.utils.extension.getCameraProvider
import com.rumble.utils.extension.videoTrimTimerTime
import kotlinx.coroutines.launch

private const val SCROLL_TRIGGER = 200

@SuppressLint("UnusedTransitionTargetStateParameter")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraGalleryScreen(
    cameraHandler: CameraHandler,
    contentHandler: ContentHandler,
    authHandler: AuthHandler,
    onOpenCameraMode: () -> Unit,
    onPreviewRecording: (uri: String) -> Unit,
    onNavigateToRegistration: (String, String, String, String) -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    BackHandler {
        contentHandler.onNavigateHome()
    }

    val permissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
            ),
            onPermissionsResult = { entries ->
                handlePermissionsResult(cameraHandler, entries)
            }
        )
    } else {
        rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
            ),
            onPermissionsResult = { entries ->
                handlePermissionsResult(cameraHandler, entries)
            }
        )
    }

    val uiState by cameraHandler.cameraHandlerUiState.collectAsStateWithLifecycle()
    val alertDialogState by cameraHandler.alertDialogState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val lazyGridState: LazyGridState = rememberLazyGridState()
    val firstVisibleItemIndex by remember { derivedStateOf { lazyGridState.firstVisibleItemIndex } }
    val firstVisibleItemScrollOffset by remember { derivedStateOf { lazyGridState.firstVisibleItemScrollOffset } }
    val compactSize = firstVisibleItemIndex != 0 || firstVisibleItemScrollOffset >= SCROLL_TRIGGER
    val transition = updateTransition(targetState = lazyGridState, label = "")
    val cameraWidth by transition.animateDp(label = "") {
        if (compactSize.not())
            videoPreviewWidth
        else
            videoPreviewCompactWidth
    }
    val cameraHeight by transition.animateDp(label = "") {
        if (compactSize.not()
        )
            videoPreviewHeight
        else
            videoPreviewCompactHeight
    }
    val iconSize by transition.animateDp(label = "") {
        if (compactSize.not()
        )
            imageXXLarge
        else
            imageXMedium
    }
    val position: Alignment.Horizontal = if (compactSize.not())
        Alignment.CenterHorizontally
    else
        Alignment.End

    val context = LocalContext.current
    val handler = Handler(Looper.getMainLooper())
    val contentObserver = object : ContentObserver(handler) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            super.onChange(selfChange, uri)
            if (permissionsGranted(permissionState)) cameraHandler.fetchGalleryVideos()
        }
    }

    var clickHandled by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.userLoggedIn) {
        if (uiState.userLoggedIn) {
            if (cameraHandler.hasUserDeniedCameraPermissionsOnce().not())
                permissionState.launchMultiplePermissionRequest()
        }
    }

    LaunchedEffect(Unit) {
        cameraHandler.cameraHandlerEventFlow.collect { event ->
            when (event) {
                is CameraHandlerVmEvent.PreviewRecording -> onPreviewRecording(event.uri)
                is CameraHandlerVmEvent.ResetClickHandled -> clickHandled = false
                else -> {}
            }
        }
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            if (permissionsGranted(permissionState)) cameraHandler.fetchGalleryVideos()
            context.contentResolver.registerContentObserver(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                true,
                contentObserver
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            context.contentResolver.unregisterContentObserver(contentObserver)
        }
    }

    if (uiState.userLoggedIn) {
        Column(
            modifier = Modifier
                .testTag(UploadGalleryTag)
                .background(MaterialTheme.colors.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding(),
                contentAlignment = Alignment.CenterEnd
            ) {
                IconButton(
                    modifier = Modifier
                        .padding(end = paddingSmall),
                    onClick = {
                        if (clickHandled.not()) {
                            clickHandled = true
                            contentHandler.onNavigateHome()
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = stringResource(id = R.string.close),
                    )
                }
            }

            Box {
                if (permissionsGranted(permissionState)) {
                    if (uiState.galleryVideos.isEmpty()) {
                        cameraHandler.fetchGalleryVideos()
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(RumbleConstants.GALLERY_ROWS_QUANTITY),
                            modifier = Modifier
                                .fillMaxSize(),
                            state = lazyGridState,
                            contentPadding = PaddingValues(paddingMedium),
                            verticalArrangement = Arrangement.spacedBy(paddingXSmall),
                            horizontalArrangement = Arrangement.spacedBy(paddingXSmall)
                        ) {
                            item(span = {
                                GridItemSpan(maxLineSpan)
                            }) {
                                Spacer(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(videoPreviewHeight)
                                )
                            }
                            item(span = {
                                GridItemSpan(maxLineSpan)
                            }) {
                                GalleryTitleView(
                                    modifier = Modifier.padding(
                                        top = paddingMedium,
                                        bottom = paddingMedium
                                    )
                                )
                            }
                            items(uiState.galleryVideos) {
                                GalleryVideoView(
                                    galleryVideoEntity = it,
                                    cameraHandler = cameraHandler,
                                )
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = paddingMedium, end = paddingMedium),
                    horizontalAlignment = position
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = paddingMedium)
                            .size(cameraWidth, cameraHeight)
                            .clip(RoundedCornerShape(radiusMedium))
                            .background(color = if (MaterialTheme.colors.isLight) enforcedDarkmo else enforcedFiord)
                    ) {
                        if (cameraPermissionsGranted(permissionState)) {
                            AndroidView(
                                modifier = Modifier
                                    .fillMaxSize(),
                                factory = { context ->
                                    val previewView = PreviewView(context).apply {
                                        layoutParams = ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.MATCH_PARENT
                                        )
                                        implementationMode =
                                            PreviewView.ImplementationMode.COMPATIBLE
                                    }

                                    val previewUseCase = Preview.Builder()
                                        .build()
                                        .also { it.setSurfaceProvider(previewView.surfaceProvider) }

                                    coroutineScope.launch {
                                        val cameraProvider = context.getCameraProvider()
                                        cameraProvider.unbindAll()
                                        if (lifecycleOwner.lifecycle.currentState != Lifecycle.State.DESTROYED) {
                                            cameraProvider.bindToLifecycle(
                                                lifecycleOwner,
                                                uiState.cameraSelector,
                                                previewUseCase
                                            )
                                        }
                                    }

                                    previewView
                                }
                            )
                        }
                        Box(modifier = Modifier.align(Alignment.Center)) {
                            CameraRecordIcon(
                                modifier = Modifier.size(iconSize)
                            ) {
                                if (clickHandled.not()) {
                                    clickHandled = true
                                    if (cameraPermissionsGranted(permissionState))
                                        onOpenCameraMode()
                                    else {
                                        cameraHandler.onDisplaySettingsDialog()
                                    }
                                }
                            }
                        }
                    }
                    if (uiState.galleryVideos.isEmpty()) {
                        GalleryTitleView(
                            modifier = Modifier.padding(start = paddingMedium, top = paddingLarge)
                        )
                        EmptyGalleryPermissionView(
                            permissionState = permissionState,
                            openSettings = cameraHandler::onOpenSettings
                        )
                    }
                }
            }
        }
    } else {
        AuthPlaceholderScreen(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            authHandler = authHandler,
            onNavigateToRegistration = onNavigateToRegistration,
            onEmailLogin = onNavigateToLogin
        )
    }

    if (alertDialogState.show) {
        if (alertDialogState.alertDialogReason is CameraAlertDialogReason.DisplaySettingsDialog) {
            PermissionsSettingsDialog(
                openSettings = {
                    cameraHandler.onOpenSettings()
                    cameraHandler.onDismissDialog()
                },
                onDismiss = cameraHandler::onDismissDialog
            )
        }
    }
}

private fun handlePermissionsResult(cameraHandler: CameraHandler, entries: Map<String, Boolean>) {
    entries.forEach { permission ->
        if (permission.value.not()) {
            cameraHandler.saveUserDeniedCameraPermission(true)
            return@forEach
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
fun permissionsGranted(permissionState: MultiplePermissionsState): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissionState.permissions.find { it.permission == Manifest.permission.READ_MEDIA_IMAGES }?.status?.isGranted == true
            && permissionState.permissions.find { it.permission == Manifest.permission.READ_MEDIA_VIDEO }?.status?.isGranted == true
    } else {
        permissionState.permissions.find { it.permission == Manifest.permission.READ_EXTERNAL_STORAGE }?.status?.isGranted == true
    }

@OptIn(ExperimentalPermissionsApi::class)
private fun cameraPermissionsGranted(permissionState: MultiplePermissionsState) =
    (permissionState.permissions.find { it.permission == Manifest.permission.CAMERA }?.status?.isGranted == true
        && permissionState.permissions.find { it.permission == Manifest.permission.RECORD_AUDIO }?.status?.isGranted == true)

@Composable
fun GalleryVideoView(
    galleryVideoEntity: GalleryVideoEntity,
    cameraHandler: CameraHandler,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1F)
            .clip(RoundedCornerShape(radiusXMedium))
            .clickable {
                cameraHandler.onPreviewRecording(
                    galleryVideoEntity.videoUri,
                    galleryVideoEntity.extension
                )
            }
    ) {
        GalleryImage(
            modifier = Modifier.fillMaxSize(),
            galleryVideoEntity = galleryVideoEntity,
            cameraHandler = cameraHandler
        )
        Row(
            modifier = Modifier
                .padding(paddingXSmall)
                .clip(RoundedCornerShape(radiusXXXMedium))
                .background(color = enforcedDarkmo)
                .align(Alignment.BottomEnd)
        ) {
            Text(
                text = galleryVideoEntity.duration.toLong().videoTrimTimerTime(),
                modifier = Modifier.padding(paddingXXSmall),
                color = enforcedWhite,
                style = tinyBodyExtraBold
            )
        }
    }
}

@Composable
private fun GalleryImage(
    modifier: Modifier = Modifier,
    galleryVideoEntity: GalleryVideoEntity,
    cameraHandler: CameraHandler
) {
    var thumbnailBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(galleryVideoEntity) {
        coroutineScope.launch {
            thumbnailBitmap = cameraHandler.getGalleryVideoThumbnail(galleryVideoEntity)
        }
    }
    AsyncImage(
        modifier = modifier.then(Modifier.background(MaterialTheme.colors.onSecondary)),
        model = thumbnailBitmap,
        contentDescription = galleryVideoEntity.title,
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun GalleryTitleView(
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier
            .fillMaxWidth(),
        text = stringResource(id = R.string.gallery).uppercase(),
        style = h6Heavy,
        textAlign = TextAlign.Start
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EmptyGalleryPermissionView(
    permissionState: MultiplePermissionsState,
    openSettings: () -> Unit
) {
    if (permissionState.permissions.find { it.permission == Manifest.permission.READ_EXTERNAL_STORAGE }?.status?.isGranted == false) {
        EmptyView(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingMedium),
            title = stringResource(id = R.string.permissions_required),
            text = stringResource(id = R.string.feature_gallery_access),
            action = EmptyViewAction(
                title = stringResource(id = R.string.open_settings),
                action = openSettings
            )
        )
    } else {
        EmptyView(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingMedium),
            title = stringResource(id = R.string.no_videos_yet),
            text = stringResource(id = R.string.tap_record_to_get_started),
        )
    }
}

@Composable
fun PermissionsSettingsDialog(openSettings: () -> Unit, onDismiss: () -> Unit) {
    RumbleAlertDialog(
        onDismissRequest = { onDismiss() },
        title = stringResource(id = R.string.permissions_required),
        text = stringResource(id = R.string.feature_requires_access_camera_mic_storage),
        actionItems = listOf(
            DialogActionItem(
                text = stringResource(id = R.string.cancel),
                action = onDismiss,
                withSpacer = true,
            ),
            DialogActionItem(
                text = stringResource(id = R.string.settings),
                action = openSettings,
                dialogActionType = DialogActionType.Positive
            )
        )
    )
}
