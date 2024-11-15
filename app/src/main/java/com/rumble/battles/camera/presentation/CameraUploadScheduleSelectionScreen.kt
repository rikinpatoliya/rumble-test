package com.rumble.battles.camera.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat.CLOCK_12H
import com.rumble.battles.R
import com.rumble.battles.UploadScheduleTag
import com.rumble.battles.commonViews.RadioSelectionRow
import com.rumble.battles.commonViews.RumbleBasicTopAppBar
import com.rumble.battles.commonViews.RumbleInputSelectorFieldView
import com.rumble.domain.camera.UploadScheduleOption
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXLarge
import com.rumble.theme.paddingXXXSmall
import com.rumble.utils.RumbleConstants.UPLOAD_DATE_PATTERN
import com.rumble.utils.RumbleConstants.UPLOAD_TIME_PATTERN
import com.rumble.utils.extension.convertToDate
import com.rumble.utils.extension.toUtcLocalMilliseconds
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant
import java.time.ZoneOffset
import java.util.TimeZone

private const val TAG = "CameraUploadScheduleSelectionScreen"

@Composable
fun CameraUploadScheduleSelectionScreen(
    cameraUploadHandler: CameraUploadHandler,
    onBackClick: () -> Unit,
) {
    val uiState by cameraUploadHandler.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(cameraUploadHandler.eventFlow) {
        cameraUploadHandler.eventFlow.collectLatest { event ->
            when (event) {
                CameraUploadVmEvent.ShowDateSelectionDialog -> {
                    showDatePicker(
                        context as FragmentActivity,
                        cameraUploadHandler,
                        uiState.selectedUploadSchedule.utcMillis
                    )
                }
                CameraUploadVmEvent.ShowTimeSelectionDialog -> {
                    showTimePicker(
                        context as FragmentActivity,
                        cameraUploadHandler,
                        uiState.selectedUploadSchedule.utcMillis,
                    )
                }
                else -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .testTag(UploadScheduleTag)
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .systemBarsPadding()
    ) {
        RumbleBasicTopAppBar(
            title = stringResource(id = R.string.schedule),
            modifier = Modifier.fillMaxWidth(),
            onBackClick = onBackClick,
        )
        LazyColumn {
            items(UploadScheduleOption.values()) { schedule ->
                RadioSelectionRow(
                    title = stringResource(id = schedule.titleId),
                    selected = schedule == uiState.selectedUploadSchedule.option,
                    onSelected = {
                        cameraUploadHandler.onScheduleSelected(schedule)
                    }
                )
            }
            if (uiState.selectedUploadSchedule.option == UploadScheduleOption.CHOOSE) {
                item {
                    Column(
                        modifier = Modifier
                            .padding(
                                top = paddingXSmall,
                                start = paddingXXXLarge + paddingXXXSmall,
                                end = paddingMedium
                            )
                    ) {
                        RumbleInputSelectorFieldView(
                            label = stringResource(id = R.string.date).uppercase(),
                            labelColor = MaterialTheme.colors.primary,
                            value = uiState.selectedUploadSchedule.utcMillis.convertToDate(pattern = UPLOAD_DATE_PATTERN),
                        ) { cameraUploadHandler.onSelectPublishDate() }
                        RumbleInputSelectorFieldView(
                            modifier = Modifier.padding(top = paddingXSmall),
                            label = stringResource(id = R.string.time).uppercase(),
                            labelColor = MaterialTheme.colors.primary,
                            value = uiState.selectedUploadSchedule.utcMillis.convertToDate(pattern = UPLOAD_TIME_PATTERN),
                            extraLabel = stringResource(id = R.string.time_zone_your_local_time),
                            extraLabelColor = MaterialTheme.colors.primaryVariant
                        ) { cameraUploadHandler.onSelectPublishTime() }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(paddingLarge))
            }
        }
    }
}

private fun showDatePicker(
    context: FragmentActivity,
    cameraUploadHandler: CameraUploadHandler,
    utcMilliseconds: Long
) {
    val utcInstant = Instant.ofEpochMilli(utcMilliseconds)
    val localTimeMillis = utcMilliseconds.toUtcLocalMilliseconds()
    val todayMillisUTC = MaterialDatePicker.todayInUtcMilliseconds()
    val today = todayMillisUTC - TimeZone.getDefault().getOffset(todayMillisUTC)
    val datePicker =
        MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(
                CalendarConstraints.Builder().setStart(today)
                    .setValidator(DateValidatorPointForward.now()).build()
            )
            .setSelection(if (localTimeMillis < today) today else localTimeMillis)
            .build()
    datePicker.addOnPositiveButtonClickListener { selection ->
        val updatedInstant = Instant.ofEpochMilli(selection).atZone(ZoneOffset.UTC)
            .withHour(utcInstant.atZone(ZoneOffset.UTC).hour)
            .withMinute(utcInstant.atZone(ZoneOffset.UTC).minute)
            .toInstant()
        cameraUploadHandler.onDateChanged(updatedInstant.toEpochMilli())
    }
    datePicker.show(context.supportFragmentManager, TAG)
}

private fun showTimePicker(
    context: FragmentActivity,
    cameraUploadHandler: CameraUploadHandler,
    utcMilliseconds: Long,
) {
    val utcInstant = Instant.ofEpochMilli(utcMilliseconds)
    val localHour = utcInstant.atZone(TimeZone.getDefault().toZoneId()).hour
    val hourDiff = utcInstant.atZone(ZoneOffset.UTC).hour - localHour

    val timePicker = MaterialTimePicker.Builder()
        .setTimeFormat(CLOCK_12H)
        .setHour(localHour)
        .setMinute(utcInstant.atZone(ZoneOffset.UTC).minute)
        .build()
    timePicker.addOnPositiveButtonClickListener {
        cameraUploadHandler.onTimeChanged(timePicker.hour + hourDiff, timePicker.minute)
    }
    timePicker.show(context.supportFragmentManager, TAG)
}