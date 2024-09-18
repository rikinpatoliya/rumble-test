package com.rumble.battles.commonViews

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.theme.RumbleTypography.h6Light
import com.rumble.utils.extension.agoString
import java.time.LocalDateTime

@Composable
fun UploadDateView(
    modifier: Modifier = Modifier,
    date: LocalDateTime,
    textStyle: TextStyle = h6Light,
    textColor: Color = MaterialTheme.colors.secondary
) {
    Text(
        modifier = modifier,
        text = date.agoString(LocalContext.current),
        style = textStyle,
        color = textColor,
    )
}

@Composable
@Preview
private fun PreviewUploadDateView() {
    val date = LocalDateTime.of(2022, 9, 19, 12, 1)
    UploadDateView(date = date)
}
