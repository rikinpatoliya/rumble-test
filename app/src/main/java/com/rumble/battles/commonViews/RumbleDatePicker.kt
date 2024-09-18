package com.rumble.battles.commonViews

import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.theme.RumbleTypography
import com.rumble.theme.enforcedCloud

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RumbleDatePicker(
    initialValue: Long,
    onDismiss: (Long?) -> Unit,
) {
    val datePickerState = rememberDatePickerState()
    if (initialValue > 0) {
        datePickerState.selectedDateMillis = initialValue
        datePickerState.displayedMonthMillis = initialValue
    }
    val confirmEnabled by remember { derivedStateOf { datePickerState.selectedDateMillis != null } }

    DatePickerDialog(
        onDismissRequest = { onDismiss(datePickerState.selectedDateMillis) },
        confirmButton = {
            TextButton(
                onClick = { onDismiss(datePickerState.selectedDateMillis) },
                enabled = confirmEnabled
            ) {
                Text(
                    text = stringResource(id = R.string.select),
                    style = RumbleTypography.h3,
                    color = if (confirmEnabled) Color.Unspecified else enforcedCloud
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss(null) }
            ) {
                Text(
                    text = stringResource(id = R.string.cancel),
                    style = RumbleTypography.h3
                )
            }
        }) {
        DatePicker(state = datePickerState)
    }
}