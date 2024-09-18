package com.rumble.battles.commonViews

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.theme.RumbleTypography
import com.rumble.theme.fierceRed
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusSmall
import com.rumble.theme.rumbleGreen

@Composable
fun RumbleInputSelectorFieldView(
    modifier: Modifier = Modifier,
    label: String = "",
    labelColor: Color = MaterialTheme.colors.onPrimary,
    backgroundColor: Color = MaterialTheme.colors.onSurface,
    textColor: Color = MaterialTheme.colors.primary,
    errorMessageColor: Color = MaterialTheme.colors.secondaryVariant,
    extraLabel: String = "",
    extraLabelColor: Color = rumbleGreen,
    extraLabelClicked: () -> Unit = {},
    value: String = "",
    hasError: Boolean = false,
    errorMessage: String = "",
    onItemClicked: () -> Unit = {},
) {

    Column(modifier = modifier) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(bottom = paddingXXXSmall),
                style = RumbleTypography.h6Heavy,
                text = label,
                color = labelColor,
                textAlign = TextAlign.Start
            )
            Text(
                modifier = Modifier
                    .padding(bottom = paddingXXXSmall)
                    .align(Alignment.BottomEnd)
                    .clickable { extraLabelClicked() },
                style = RumbleTypography.tinyBody,
                text = extraLabel,
                color = extraLabelColor,
                textAlign = TextAlign.End
            )
        }

        Box(modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(radiusSmall))
            .clickable { onItemClicked() }) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(radiusSmall),
                value = value,
                enabled = false,
                onValueChange = {},
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = backgroundColor,
                    textColor = textColor,
                    disabledTextColor = textColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = fierceRed
                ),
                textStyle = RumbleTypography.body1,
                isError = hasError,
            )
        }

        if (hasError) {
            ErrorMessageView(
                modifier = Modifier
                    .padding(top = paddingXSmall)
                    .fillMaxWidth(),
                errorMessage = errorMessage,
                textColor = errorMessageColor
            )
        }
    }
}

@Composable
@Preview(device = Devices.DEFAULT, showSystemUi = true)
private fun PreviewUserNameEmailView() {

    RumbleInputSelectorFieldView(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingMedium),
        label = "EMAIL OR USERNAME",
        value = "The Dan Bongino Show",
        hasError = true,
        errorMessage = "Some error message"
    )
}