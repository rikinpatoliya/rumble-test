package com.rumble.battles.camera.presentation

import android.app.Application
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import android.view.OrientationEventListener.ORIENTATION_UNKNOWN
import android.view.Surface
import androidx.camera.core.CameraSelector
import androidx.camera.video.Recording
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.battles.camera.presentation.CameraHandlerUIState.Companion.INITIAL_TIMER_TIME
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.dialogs.AlertDialogState
import com.rumble.battles.landing.RumbleOrientationChangeHandler
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.camera.GalleryVideoEntity
import com.rumble.domain.camera.UploadLicense
import com.rumble.domain.camera.UploadSchedule
import com.rumble.domain.camera.UploadScheduleOption
import com.rumble.domain.camera.UploadStatus
import com.rumble.domain.camera.UploadVisibility
import com.rumble.domain.camera.domain.usecases.SaveVideoUseCase
import com.rumble.domain.camera.domain.usecases.UploadVideoUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UserUploadChannelEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UserUploadChannelsResult
import com.rumble.domain.channels.channeldetails.domain.usecase.GetUserUploadChannelsUseCase
import com.rumble.domain.common.domain.usecase.CheckCurrentDateAndAdjustUseCase
import com.rumble.domain.common.domain.usecase.CombineTimeWithDateUseCase
import com.rumble.domain.common.domain.usecase.OpenPhoneSettingUseCase
import com.rumble.domain.profile.domain.GetUserProfileUseCase
import com.rumble.domain.profile.domainmodel.UserProfileEntity
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.domain.uploadmanager.dto.UploadVideoData
import com.rumble.domain.video.domain.usecases.CreateTempDirectoryUseCase
import com.rumble.domain.video.domain.usecases.CreateTempThumbnailFileUseCase
import com.rumble.domain.video.domain.usecases.GeneratePlaceholderThumbnailsUseCase
import com.rumble.domain.video.domain.usecases.GenerateThumbnailUseCase
import com.rumble.domain.video.domain.usecases.GetExtractThumbnailTimesUseCase
import com.rumble.domain.video.domain.usecases.GetGalleryThumbnailUseCase
import com.rumble.domain.video.domain.usecases.GetGalleryVideosUseCase
import com.rumble.domain.video.domain.usecases.GetTrimBitmapDataUseCase
import com.rumble.domain.video.domain.usecases.SaveThumbnailToFileUseCase
import com.rumble.network.session.SessionManager
import com.rumble.utils.RumbleConstants
import com.rumble.utils.RumbleConstants.MAX_CHARACTERS_UPLOAD_DESCRIPTION
import com.rumble.utils.RumbleConstants.MAX_CHARACTERS_UPLOAD_TITLE
import com.rumble.utils.RumbleConstants.RUMBLE_MINIMUM_VIDEO_TRIM_LENGTH_MILLIS
import com.rumble.utils.RumbleConstants.RUMBLE_VIDEO_EXTENSION
import com.rumble.utils.RumbleConstants.VIDEO_UPLOAD_PREVIEW_THUMBNAILS_QUANTITY
import com.rumble.utils.extension.videoRecordTimerTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

interface CameraHandler {
    val cameraHandlerEventFlow: Flow<CameraHandlerVmEvent>
    val cameraHandlerUiState: StateFlow<CameraHandlerUIState>
    val alertDialogState: StateFlow<AlertDialogState>

    fun onOpenSettings()
    fun onDisplaySettingsDialog()
    fun onDismissDialog()
    fun onPreviewRecording(uri: Uri, extension: String)
    fun onNextToStepOne(uri: String)
    fun generateTrimThumbnails(uri: String, size: IntSize)
    fun clearTrimData()
    fun updateDuration(recordedDurationNanos: Long)
    fun toggleFlash(enable: Boolean)
    fun startRecording(recording: Recording?)
    fun stopRecording()
    fun stopRecordingWithError(exception: Throwable)
    fun toggleCameraSelector(cameraSelector: CameraSelector)
    fun cancelRecording()
    fun fetchGalleryVideos()
    suspend fun getGalleryVideoThumbnail(galleryVideoEntity: GalleryVideoEntity): Bitmap?
    fun updateCurrentPosition(value: Float)
    fun updateStartPosition(value: Float)
    fun updateEndPosition(value: Float)
    fun updateSlidingState(value: Boolean)
    fun updateSliderPosition(value: ClosedFloatingPointRange<Float>, initial: Boolean = false)
    fun updateLoopVideoState(value: Boolean)
    fun hasUserDeniedCameraPermissionsOnce(): Boolean
    fun saveUserDeniedCameraPermission(denied: Boolean)
}

interface CameraUploadHandler {
    val uiState: StateFlow<UserUploadUIState>
    val eventFlow: Flow<CameraUploadVmEvent>
    fun onUploadChannelSelected(channelId: String)
    fun onTitleChanged(value: String)
    fun onDescriptionChanged(value: String)
    fun onExclusiveAgreementCheckedChanged(value: Boolean)
    fun onTermsOfServiceCheckedChanged(value: Boolean)
    fun onLicenseSelected(uploadLicense: UploadLicense)
    fun onVisibilitySelected(uploadVisibility: UploadVisibility)
    fun onScheduleSelected(uploadScheduleOption: UploadScheduleOption)
    fun onPublishClicked(onPublish: () -> Unit)
    fun onNextClicked(onNext: () -> Unit)
    fun onBackClicked(onBack: () -> Unit)
    fun onSelectPublishDate()
    fun onSelectPublishTime()
    fun onDateChanged(newUtcMillis: Long)
    fun onTimeChanged(hour: Int, minute: Int)
    fun onSelectUploadThumbnail(bitmap: Bitmap)
    fun onUploadImageChanged(uri: Uri?)
    suspend fun generateUIThumbnails()
}

sealed class CameraAlertDialogReason : AlertDialogReason {
    object DisplaySettingsDialog : CameraAlertDialogReason()
}

sealed class CameraHandlerVmEvent {
    object ProceedToStepOne : CameraHandlerVmEvent()
    data class PreviewRecording(val uri: String) : CameraHandlerVmEvent()
    object ResetClickHandled : CameraHandlerVmEvent()
    object Error : CameraHandlerVmEvent()
    object RecordingError : CameraHandlerVmEvent()
    object RecordingStartError : CameraHandlerVmEvent()
}

data class CameraHandlerUIState(
    val loading: Boolean = false,
    val galleryVideos: List<GalleryVideoEntity> = emptyList(),
    val rotation: Int = Surface.ROTATION_0,
    var recording: Recording? = null,
    val duration: Long = 0L,
    val timerText: String = INITIAL_TIMER_TIME,
    val flashEnabled: Boolean = false,
    val recordingStarted: Boolean = false,
    val recordingCancelled: Boolean = false,
    val cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    val trimThumbnails: List<Bitmap> = emptyList(),
    val trimBarData: TrimBarData = TrimBarData(),
    val userLoggedIn: Boolean = false,
    val userName: String = "",
    val userPicture: String = "",
) {
    companion object {
        const val INITIAL_TIMER_TIME: String = "00:00"
    }
}

data class TrimBarData(
    val isSliderPositionChanging: Boolean = false,
    val currentPosition: Float = 0F,
    val sliderPosition: ClosedFloatingPointRange<Float> = 0F..1F,
    val loopVideo: Boolean = true
)

data class UserUploadUIState(
    val uploadVideoUri: String = "",
    val videoExtension: String = RUMBLE_VIDEO_EXTENSION,
    val thumbnails: List<Bitmap> = emptyList(),
    val generateUIThumbs: Boolean = true,
    val selectedThumbnail: Bitmap? = null,
    val selectedUploadImage: Uri? = null,
    val title: String = "",
    val description: String = "",
    val titleError: Boolean = false,
    val titleEmptyError: Boolean = false,
    val descriptionError: Boolean = false,
    val exclusiveAgreementChecked: Boolean = false,
    val termsOfServiceChecked: Boolean = false,
    val exclusiveAgreementError: Boolean = false,
    val termsOfServiceError: Boolean = false,
    val userProfile: UserProfileEntity? = null,
    val userUploadProfile: UserUploadChannelEntity,
    val userUploadChannels: List<UserUploadChannelEntity>,
    val selectedUploadChannel: UserUploadChannelEntity,
    val selectedUploadLicense: UploadLicense = UploadLicense.RUMBLE_ONLY,
    val selectedUploadVisibility: UploadVisibility = UploadVisibility.PUBLIC,
    val selectedUploadSchedule: UploadSchedule = UploadSchedule(option = UploadScheduleOption.NOW),
    val loading: Boolean = false,
)

sealed class CameraUploadVmEvent {
    object ShowDateSelectionDialog : CameraUploadVmEvent()
    object ShowTimeSelectionDialog : CameraUploadVmEvent()
}

private const val TAG_UPLOAD_VM = "CameraUploadViewModel"
private const val TAG_CAMERA_VM = "CameraViewModel"

@HiltViewModel
class CameraViewModel @Inject constructor(
    application: Application,
    private val sessionManager: SessionManager,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val getUserUploadChannelsUseCase: GetUserUploadChannelsUseCase,
    private val openPhoneSettingUseCase: OpenPhoneSettingUseCase,
    private val getTrimBitmapDataUseCase: GetTrimBitmapDataUseCase,
    private val generateThumbnailUseCase: GenerateThumbnailUseCase,
    private val getExtractThumbnailTimesUseCase: GetExtractThumbnailTimesUseCase,
    private val generatePlaceholderThumbnailsUseCase: GeneratePlaceholderThumbnailsUseCase,
    private val getGalleryVideosUseCase: GetGalleryVideosUseCase,
    private val getGalleryThumbnailUseCase: GetGalleryThumbnailUseCase,
    private val uploadVideoUseCase: UploadVideoUseCase,
    private val saveVideoUseCase: SaveVideoUseCase,
    private val userPreferenceManager: UserPreferenceManager,
    private val saveThumbnailToFileUseCase: SaveThumbnailToFileUseCase,
    private val createTempDirectoryUseCase: CreateTempDirectoryUseCase,
    private val createTempThumbnailFileUseCase: CreateTempThumbnailFileUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val combineTimeWithDateUseCase: CombineTimeWithDateUseCase,
    private val checkCurrentDateAndAdjustUseCase: CheckCurrentDateAndAdjustUseCase
) : ViewModel(), CameraUploadHandler, CameraHandler {

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        handleFailure(throwable)
    }
    private val orientationEventListener: RumbleOrientationChangeHandler

    private var uploadVideoData = UploadVideoData(
        uploadUUID = UUID.randomUUID().toString(),
        videoUri = "",
        videoExtension = RUMBLE_VIDEO_EXTENSION,
        licence = UploadLicense.RUMBLE_ONLY.apiValue,
        visibility = UploadVisibility.PUBLIC.apiValue
    )

    override val uiState = MutableStateFlow(
        UserUploadUIState(
            userUploadProfile = createDefaultUserUploadChannelEntity(),
            userUploadChannels = emptyList(),
            selectedUploadChannel = createDefaultUserUploadChannelEntity()
        )
    )

    override val eventFlow: MutableSharedFlow<CameraUploadVmEvent> = MutableSharedFlow()

    override val cameraHandlerEventFlow: MutableSharedFlow<CameraHandlerVmEvent> =
        MutableSharedFlow()

    override val cameraHandlerUiState = MutableStateFlow(CameraHandlerUIState())

    override val alertDialogState = MutableStateFlow(AlertDialogState())

    init {
        observeUserLoginState()
        orientationEventListener = RumbleOrientationChangeHandler(application) {
            val rotation = when (it) {
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE -> Surface.ROTATION_270
                ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT -> Surface.ROTATION_180
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> Surface.ROTATION_90
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> Surface.ROTATION_0
                else -> ORIENTATION_UNKNOWN
            }
            if (rotation == ORIENTATION_UNKNOWN) return@RumbleOrientationChangeHandler
            onRotationChange(rotation)
        }
        orientationEventListener.enable()
        fetchUserUploadChannels()
        viewModelScope.launch(errorHandler) {
            fetchUserProfile()
        }
        observeUserProfile()
    }

    override fun onCleared() {
        orientationEventListener.disable()
        super.onCleared()
    }

    private fun onRotationChange(rotation: Int) {
        cameraHandlerUiState.update {
            it.copy(
                rotation = rotation
            )
        }
    }

    override fun onOpenSettings() = openPhoneSettingUseCase()

    override fun fetchGalleryVideos() {
        val videos = getGalleryVideosUseCase()
        cameraHandlerUiState.update {
            it.copy(
                galleryVideos = videos
            )
        }
    }

    override suspend fun getGalleryVideoThumbnail(galleryVideoEntity: GalleryVideoEntity): Bitmap? =
        getGalleryThumbnailUseCase(galleryVideoEntity)

    override fun updateCurrentPosition(value: Float) {
        cameraHandlerUiState.update {
            it.copy(
                trimBarData = it.trimBarData.copy(
                    currentPosition = value,
                )
            )
        }
    }

    override fun updateStartPosition(value: Float) {
        if (value < cameraHandlerUiState.value.trimBarData.sliderPosition.start) {
            cameraHandlerUiState.update {
                it.copy(
                    trimBarData = it.trimBarData.copy(
                        isSliderPositionChanging = true,
                        sliderPosition = value..it.trimBarData.sliderPosition.endInclusive
                    )
                )
            }
        }
    }

    override fun updateEndPosition(value: Float) {
        if (value > cameraHandlerUiState.value.trimBarData.sliderPosition.endInclusive) {
            cameraHandlerUiState.update {
                it.copy(
                    trimBarData = it.trimBarData.copy(
                        isSliderPositionChanging = true,
                        sliderPosition = it.trimBarData.sliderPosition.start..value
                    )
                )
            }
        }
    }

    override fun updateSlidingState(value: Boolean) {
        cameraHandlerUiState.update {
            it.copy(
                trimBarData = it.trimBarData.copy(
                    isSliderPositionChanging = value
                )
            )
        }
    }

    override fun updateSliderPosition(value: ClosedFloatingPointRange<Float>, initial: Boolean) {
        val start = cameraHandlerUiState.value.trimBarData.sliderPosition.start
        val end = cameraHandlerUiState.value.trimBarData.sliderPosition.endInclusive
        if (initial
            || value.start < start
            || value.endInclusive > end
            || end - start > RUMBLE_MINIMUM_VIDEO_TRIM_LENGTH_MILLIS
        ) {
            val currentPosition = cameraHandlerUiState.value.trimBarData.currentPosition
            val newCurrentPosition =
                when {
                    currentPosition < value.start -> value.start
                    currentPosition > value.endInclusive -> value.endInclusive
                    else -> currentPosition
                }

            cameraHandlerUiState.update {
                it.copy(
                    trimBarData = it.trimBarData.copy(
                        isSliderPositionChanging = initial.not(),
                        sliderPosition = value,
                        currentPosition = newCurrentPosition
                    )
                )
            }
        }
    }

    override fun updateLoopVideoState(value: Boolean) {
        cameraHandlerUiState.update {
            it.copy(
                trimBarData = it.trimBarData.copy(loopVideo = value)
            )
        }
    }

    override fun hasUserDeniedCameraPermissionsOnce(): Boolean =
        runBlocking { userPreferenceManager.cameraPermissionRequestDenied.first() }

    override fun saveUserDeniedCameraPermission(denied: Boolean) {
        viewModelScope.launch {
            userPreferenceManager.saveCameraPermissionRequestDenied(denied)
        }
    }

    override fun onDisplaySettingsDialog() {
        alertDialogState.value = AlertDialogState(
            show = true,
            alertDialogReason = CameraAlertDialogReason.DisplaySettingsDialog
        )
    }

    override fun onDismissDialog() {
        emitCameraHandlerVmEvent(CameraHandlerVmEvent.ResetClickHandled)
        alertDialogState.value = AlertDialogState()
    }

    override fun onPreviewRecording(uri: Uri, extension: String) {
        uiState.update {
            it.copy(videoExtension = extension)
        }
        emitCameraHandlerVmEvent(CameraHandlerVmEvent.PreviewRecording(uri.toString()))
    }

    override suspend fun generateUIThumbnails() {
        viewModelScope.launch(errorHandler) {
            uiState.update {
                it.copy(
                    thumbnails = generatePlaceholderThumbnailsUseCase(
                        quantity = VIDEO_UPLOAD_PREVIEW_THUMBNAILS_QUANTITY,
                    ),
                    generateUIThumbs = false
                )
            }
            val thumbExtractTimes = getExtractThumbnailTimesUseCase(
                duration = cameraHandlerUiState.value.duration,
                quantity = VIDEO_UPLOAD_PREVIEW_THUMBNAILS_QUANTITY
            )
            val uri = uiState.value.uploadVideoUri
            thumbExtractTimes.mapIndexed { bitmapIndex, time ->
                val newBitmap = generateThumbnailUseCase(uri, time)
                newBitmap?.let {
                    uiState.update {
                        it.copy(
                            thumbnails = uiState.value.thumbnails.mapIndexed { index, bitmap ->
                                if (bitmapIndex == index)
                                    newBitmap
                                else
                                    bitmap
                            },
                        )
                    }
                    if (bitmapIndex == 0) {
                        uiState.update {
                            it.copy(
                                selectedThumbnail = newBitmap,
                                selectedUploadImage = null
                            )
                        }
                        onSaveVideoDraft(newBitmap)
                    }
                }
            }
        }
    }

    override fun onNextToStepOne(uri: String) {
        uiState.update {
            it.copy(
                uploadVideoUri = uri,
                generateUIThumbs = uiState.value.uploadVideoUri != uri
            )
        }
        emitCameraHandlerVmEvent(CameraHandlerVmEvent.ProceedToStepOne)
    }

    override fun generateTrimThumbnails(uri: String, size: IntSize) {
        viewModelScope.launch(errorHandler) {
            val trimBitmapData = getTrimBitmapDataUseCase(uri, size)
            cameraHandlerUiState.update {
                it.copy(
                    loading = false,
                    trimThumbnails = generatePlaceholderThumbnailsUseCase(
                        quantity = trimBitmapData.quantity,
                        width = trimBitmapData.width,
                        height = trimBitmapData.height
                    ),
                )
            }
            trimBitmapData.thumbExtractTimes.mapIndexed { index, time ->
                val newBitmap = generateThumbnailUseCase(uri, time, trimBitmapData)
                newBitmap?.let {
                    cameraHandlerUiState.update {
                        it.copy(
                            loading = false,
                            trimThumbnails = cameraHandlerUiState.value.trimThumbnails.mapIndexed { bitmapIndex, bitmap ->
                                if (bitmapIndex == index)
                                    newBitmap
                                else
                                    bitmap
                            }
                        )
                    }
                }
            }
        }
    }

    override fun clearTrimData() {
        cameraHandlerUiState.update {
            it.copy(
                trimThumbnails = emptyList(),
                trimBarData = TrimBarData()
            )
        }
    }

    override fun updateDuration(recordedDurationNanos: Long) {
        cameraHandlerUiState.update {
            it.copy(
                duration = recordedDurationNanos,
                timerText = recordedDurationNanos.videoRecordTimerTime()
            )
        }
    }

    override fun toggleFlash(enable: Boolean) {
        cameraHandlerUiState.update {
            it.copy(
                flashEnabled = enable
            )
        }
    }

    override fun startRecording(recording: Recording?) {
        orientationEventListener.disable()
        recording?.let {
            cameraHandlerUiState.update {
                it.copy(
                    recording = recording,
                    recordingStarted = true,
                    recordingCancelled = false,
                )
            }
        } ?: kotlin.run {
            unhandledErrorUseCase(
                TAG_CAMERA_VM,
                Throwable("CameraHandlerVmEvent.RecordingStartError")
            )
            emitCameraHandlerVmEvent(CameraHandlerVmEvent.RecordingStartError)
        }
    }

    override fun stopRecording() {
        cameraHandlerUiState.value.recording?.let { recording ->
            recording.stop()
            cameraHandlerUiState.update {
                it.copy(
                    recording = null,
                    timerText = INITIAL_TIMER_TIME,
                    recordingStarted = false
                )
            }
        }
        orientationEventListener.enable()
    }

    override fun stopRecordingWithError(exception: Throwable) {
        unhandledErrorUseCase(TAG_CAMERA_VM, exception)
        stopRecording()
        emitCameraHandlerVmEvent(CameraHandlerVmEvent.RecordingError)
    }

    override fun cancelRecording() {
        cameraHandlerUiState.update {
            it.copy(
                recordingCancelled = true
            )
        }
        stopRecording()
    }

    override fun toggleCameraSelector(cameraSelector: CameraSelector) {
        cameraHandlerUiState.update {
            it.copy(
                cameraSelector = cameraSelector
            )
        }
    }

    override fun onSelectUploadThumbnail(bitmap: Bitmap) {
        uiState.update {
            it.copy(
                selectedThumbnail = bitmap,
                selectedUploadImage = null
            )
        }
        viewModelScope.launch(errorHandler) {
            uploadVideoData.tempThumbUrl?.let {
                saveThumbnailToFileUseCase(it, bitmap)
            }
        }
    }

    override fun onUploadImageChanged(uri: Uri?) {
        uri?.let { imageUri ->
            uiState.update {
                it.copy(
                    selectedThumbnail = null,
                    selectedUploadImage = imageUri
                )
            }
            viewModelScope.launch(errorHandler) {
                uploadVideoData.tempThumbUrl?.let {
                    saveThumbnailToFileUseCase(it, imageUri)
                }
            }
        }
    }

    private fun observeUserLoginState() {
        viewModelScope.launch {
            sessionManager.cookiesFlow.distinctUntilChanged().collectLatest { cookies ->
                cameraHandlerUiState.update {
                    it.copy(userLoggedIn = cookies.isNotEmpty())
                }
            }
        }
    }

    private fun observeUserProfile() {
        viewModelScope.launch {
            sessionManager.userIdFlow.distinctUntilChanged().collectLatest { userId ->
                uiState.update {
                    it.copy(
                        userUploadProfile = it.userUploadProfile.copy(id = userId),
                        selectedUploadChannel = it.selectedUploadChannel.copy(id = userId)
                    )
                }
            }
        }
        viewModelScope.launch {
            sessionManager.userNameFlow.distinctUntilChanged().collectLatest { userName ->
                uiState.update {
                    it.copy(
                        userUploadProfile = it.userUploadProfile.copy(
                            title = userName,
                            name = userName
                        ),
                        selectedUploadChannel = it.selectedUploadChannel.copy(
                            title = userName,
                            name = userName
                        )
                    )
                }
            }
        }
        viewModelScope.launch {
            sessionManager.userPictureFlow.distinctUntilChanged().collectLatest { userPicture ->
                uiState.update {
                    it.copy(
                        userUploadProfile = it.userUploadProfile.copy(thumbnail = userPicture),
                        selectedUploadChannel = it.selectedUploadChannel.copy(thumbnail = userPicture)
                    )
                }
            }
        }
    }

    private fun onSaveVideoDraft(bitmap: Bitmap) {
        val outputDirectoryName = createTempDirectoryUseCase(uploadVideoData.uploadUUID)
        val tempThumbUrl = createTempThumbnailFileUseCase(outputDirectoryName)
        uploadVideoData = uploadVideoData.copy(
            videoUri = uiState.value.uploadVideoUri,
            tempThumbUrl = tempThumbUrl
        )
        saveDraftToDB()
        viewModelScope.launch(errorHandler) {
            saveThumbnailToFileUseCase(tempThumbUrl, bitmap)
        }
    }

    override fun onUploadChannelSelected(channelId: String) {
        uiState.update { state ->
            if (state.userUploadProfile.id == channelId) {
                state.copy(
                    selectedUploadChannel = state.userUploadProfile
                )
            } else {
                state.copy(
                    selectedUploadChannel = state.userUploadChannels.find {
                        it.id == channelId
                    } ?: state.selectedUploadChannel
                )
            }
        }
        uploadVideoData = uploadVideoData.copy(
            channelId = uiState.value.selectedUploadChannel.channelId
        )
        saveDraftToDB()
    }

    override fun onTitleChanged(value: String) {
        val existingTitleEmptyError = uiState.value.titleEmptyError
        val existingTitleError = uiState.value.titleError
        uiState.update {
            it.copy(
                title = value,
                titleEmptyError = if (existingTitleEmptyError) value.isBlank() else false,
                titleError = if (existingTitleError) value.count() > MAX_CHARACTERS_UPLOAD_TITLE else false
            )
        }
        uploadVideoData = uploadVideoData.copy(
            title = value
        )
        saveDraftToDB()
    }

    override fun onDescriptionChanged(value: String) {
        val existingDescriptionError = uiState.value.descriptionError
        uiState.update {
            it.copy(
                description = value,
                descriptionError = if (existingDescriptionError) value.count() > MAX_CHARACTERS_UPLOAD_DESCRIPTION else false
            )
        }
        uploadVideoData = uploadVideoData.copy(
            description = value
        )
        saveDraftToDB()
    }

    override fun onExclusiveAgreementCheckedChanged(value: Boolean) {
        uiState.update {
            it.copy(
                exclusiveAgreementChecked = value,
                exclusiveAgreementError = !value,
            )
        }
        uploadVideoData = uploadVideoData.copy(
            rightsAccepted = uiState.value.exclusiveAgreementChecked
        )
        saveDraftToDB()
    }

    override fun onTermsOfServiceCheckedChanged(value: Boolean) {
        uiState.update {
            it.copy(
                termsOfServiceChecked = value,
                termsOfServiceError = !value,
            )
        }
        uploadVideoData = uploadVideoData.copy(
            termsAccepted = uiState.value.termsOfServiceChecked
        )
        saveDraftToDB()
    }

    override fun onLicenseSelected(uploadLicense: UploadLicense) {
        uiState.update {
            it.copy(
                selectedUploadLicense = uploadLicense
            )
        }
        uploadVideoData = uploadVideoData.copy(
            licence = uploadLicense.apiValue
        )
        saveDraftToDB()

    }

    override fun onVisibilitySelected(uploadVisibility: UploadVisibility) {
        uiState.update {
            it.copy(
                selectedUploadVisibility = uploadVisibility
            )
        }
        uploadVideoData = uploadVideoData.copy(
            visibility = uploadVisibility.apiValue
        )
        saveDraftToDB()
    }

    override fun onScheduleSelected(uploadScheduleOption: UploadScheduleOption) {
        val utcNow = Instant.now().toEpochMilli()
        val publishUtcMillis =
            if (uiState.value.selectedUploadSchedule.utcMillis < utcNow) utcNow else uiState.value.selectedUploadSchedule.utcMillis
        uiState.update { state ->
            state.copy(
                selectedUploadSchedule = UploadSchedule(
                    option = uploadScheduleOption,
                    utcMillis = publishUtcMillis
                )
            )
        }
        uploadVideoData = uploadVideoData.copy(
            publishDate = if (uploadScheduleOption == UploadScheduleOption.NOW) null else publishUtcMillis
        )
        saveDraftToDB()
    }

    override fun onSelectPublishDate() {
        emitVmEvent(CameraUploadVmEvent.ShowDateSelectionDialog)
    }

    override fun onSelectPublishTime() {
        emitVmEvent(CameraUploadVmEvent.ShowTimeSelectionDialog)
    }

    override fun onDateChanged(newUtcMillis: Long) {
        val adjustedMillis = checkCurrentDateAndAdjustUseCase(newUtcMillis)
        uiState.update {
            it.copy(
                selectedUploadSchedule = uiState.value.selectedUploadSchedule.copy(
                    utcMillis = adjustedMillis
                )
            )
        }
        uploadVideoData = uploadVideoData.copy(
            publishDate = adjustedMillis
        )
        saveDraftToDB()
    }

    override fun onTimeChanged(hour: Int, minute: Int) {
        viewModelScope.launch(errorHandler) {
            val utcNow = Instant.now().toEpochMilli()
            val newUtcMillis = combineTimeWithDateUseCase(
                hour = hour,
                minute = minute,
                utcMillis = uiState.value.selectedUploadSchedule.utcMillis
            )
            uiState.update {
                it.copy(
                    selectedUploadSchedule = uiState.value.selectedUploadSchedule.copy(
                        utcMillis = if (newUtcMillis > utcNow) newUtcMillis else uiState.value.selectedUploadSchedule.utcMillis
                    )
                )
            }
            uploadVideoData = uploadVideoData.copy(
                publishDate = uiState.value.selectedUploadSchedule.utcMillis
            )
            saveDraftToDB()
        }
    }

    override fun onPublishClicked(onPublish: () -> Unit) {
        if (uiState.value.exclusiveAgreementChecked && uiState.value.termsOfServiceChecked) {
            viewModelScope.launch(errorHandler) {
                val uri = uiState.value.uploadVideoUri
                if (uri.isNotEmpty()) {
                    val uploadQuality =
                        runBlocking { userPreferenceManager.uploadQualityFlow.first() }
                    uploadVideoData = uploadVideoData.copy(
                        status = UploadStatus.PROCESSING,
                        uploadQuality = uploadQuality
                    )
                    uploadVideoUseCase(uploadVideoData)
                    saveDraftToDB()
                }
            }
            resetForm()
            onPublish()
        } else {
            uiState.update {
                it.copy(
                    exclusiveAgreementError = !it.exclusiveAgreementChecked,
                    termsOfServiceError = !it.termsOfServiceChecked
                )
            }
        }
    }

    override fun onNextClicked(onNext: () -> Unit) {
        val title = uiState.value.title
        val description = uiState.value.description
        val titleEmptyError = title.isBlank()
        val titleError = title.count() > MAX_CHARACTERS_UPLOAD_TITLE
        val descriptionError = description.count() > MAX_CHARACTERS_UPLOAD_DESCRIPTION
        uiState.update {
            it.copy(
                titleEmptyError = titleEmptyError,
                titleError = titleError,
                descriptionError = descriptionError
            )
        }

        if (titleEmptyError.not() && titleError.not() && descriptionError.not()) {
            onNext()
        }
    }

    override fun onBackClicked(onBack: () -> Unit) {
        uiState.update {
            it.copy(
                titleEmptyError = false,
                titleError = false,
                descriptionError = false
            )
        }
        onBack()
    }

    private suspend fun fetchUserProfile() {
        val result = getUserProfileUseCase()
        if (result.success) {
            uiState.update {
                it.copy(
                    userProfile = result.userProfileEntity
                )
            }
        }
    }

    private fun fetchUserUploadChannels() {
        viewModelScope.launch(errorHandler) {
            when (val result = getUserUploadChannelsUseCase()) {
                is UserUploadChannelsResult.UserUploadChannelsError -> {
                    delay(RumbleConstants.RETRY_DELAY_USER_UPLOAD_CHANNELS)
                    fetchUserUploadChannels()
                }

                is UserUploadChannelsResult.UserUploadChannelsSuccess -> {
                    uiState.update {
                        it.copy(
                            userUploadChannels = result.userUploadChannels
                        )
                    }
                }

                else -> {}
            }
        }
    }

    private fun handleFailure(throwable: Throwable) {
        unhandledErrorUseCase(TAG_UPLOAD_VM, throwable)
    }

    private fun createDefaultUserUploadChannelEntity() =
        UserUploadChannelEntity(id = "", channelId = 0, title = "", name = "", thumbnail = null)


    private fun emitVmEvent(event: CameraUploadVmEvent) =
        viewModelScope.launch { eventFlow.emit(event) }

    private fun emitCameraHandlerVmEvent(event: CameraHandlerVmEvent) =
        viewModelScope.launch { cameraHandlerEventFlow.emit(event) }

    private fun saveDraftToDB() {
        viewModelScope.launch(errorHandler) {
            saveVideoUseCase(
                uploadVideoData.copy(
                    trimStart = cameraHandlerUiState.value.trimBarData.sliderPosition.start,
                    trimEnd = cameraHandlerUiState.value.trimBarData.sliderPosition.endInclusive
                )
            )
        }
    }

    private fun resetForm() {
        uiState.value = uiState.value.copy(
            uploadVideoUri = "",
            videoExtension = RUMBLE_VIDEO_EXTENSION,
            thumbnails = emptyList(),
            generateUIThumbs = true,
            selectedThumbnail = null,
            selectedUploadImage = null,
            title = "",
            description = "",
            titleError = false,
            titleEmptyError = false,
            descriptionError = false,
            exclusiveAgreementChecked = false,
            termsOfServiceChecked = false,
            exclusiveAgreementError = false,
            termsOfServiceError = false,
            selectedUploadLicense = UploadLicense.RUMBLE_ONLY,
            selectedUploadVisibility = UploadVisibility.PUBLIC,
            selectedUploadSchedule = UploadSchedule(option = UploadScheduleOption.NOW)
        )
    }
}