package com.rumble.battles.commonViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.commandiron.wheel_picker_compose.WheelDatePicker
import com.rumble.theme.RumbleTheme
import com.rumble.theme.enforcedBlack
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingXXXLarge
import com.rumble.theme.radiusXXXMedium
import com.rumble.utils.extension.toUtcLocalDate
import com.rumble.utils.extension.toUtcLong
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RumbleWheelDatePicker(
    initialValue: Long = 0,
    onDismissRequest: () -> Unit,
    onChanged: (Long) -> Unit = {}
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        dragHandle = null,
        containerColor = Color.Transparent
    ) {
        RumbleWheelDatePicker(
            initialValue = initialValue,
            onChanged = onChanged
        )
    }
}

@Composable
fun RumbleWheelDatePicker(
    initialValue: Long = 0,
    onChanged: (Long) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .padding(start = paddingLarge, end = paddingLarge, bottom = paddingXXXLarge)
            .fillMaxWidth()
            .clip(RoundedCornerShape(radiusXXXMedium))
            .background(enforcedBlack)
    ) {
        WheelDatePicker(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingLarge),
            textColor = enforcedWhite,
            startDate = if (initialValue != 0L) initialValue.toUtcLocalDate() else LocalDate.now(),
            yearsRange = IntRange(1900, LocalDate.now().year),
            maxDate = LocalDate.now()
        ) { snappedDate ->
            onChanged(snappedDate.toUtcLong())
        }
    }
}

@Composable
@Preview
private fun Preview() {
    RumbleTheme {
        RumbleWheelDatePicker()
    }
}