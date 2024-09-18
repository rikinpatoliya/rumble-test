package com.rumble.battles.commonViews

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.InputFieldErrorTag
import com.rumble.battles.R
import com.rumble.theme.RumbleTypography
import com.rumble.theme.fierceRed
import com.rumble.theme.imageMedium
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusSmall

@Composable
fun RumbleInputFieldView(
    modifier: Modifier = Modifier,
    testTag: String = "",
    label: String? = null,
    labelColor: Color = MaterialTheme.colors.primary,
    backgroundColor: Color = MaterialTheme.colors.onSurface,
    textColor: Color = MaterialTheme.colors.primary,
    cursorColor: Color = MaterialTheme.colors.primary,
    iconTintColor: Color = MaterialTheme.colors.secondary,
    errorMessageColor: Color = MaterialTheme.colors.secondary,
    initialValue: String = "",
    onValueChange: (String) -> Unit = {},
    hasError: Boolean = false,
    errorMessage: String = "",
    showTrailingIcon: Boolean = true,
) {

    var text by remember { mutableStateOf(initialValue) }

    Column(modifier = modifier) {
        label?.let {
            Text(
                modifier = Modifier.padding(bottom = paddingXXXSmall),
                style = RumbleTypography.h6Heavy,
                text = label,
                color = labelColor
            )
        }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag),
            shape = RoundedCornerShape(radiusSmall),
            value = text,
            onValueChange = {
                onValueChange(it)
                text = it
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = backgroundColor,
                cursorColor = cursorColor,
                textColor = textColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = fierceRed
            ),
            textStyle = RumbleTypography.body1,
            isError = hasError,
            trailingIcon = {
                if (text.isNotBlank() && showTrailingIcon) {
                    IconButton(
                        onClick = {
                            text = ""
                            onValueChange("")
                        },
                        modifier = Modifier.size(imageMedium)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_clear_text),
                            contentDescription = stringResource(id = R.string.clear_text),
                            tint = iconTintColor
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                autoCorrect = true,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        if (hasError) {
            ErrorMessageView(
                modifier = Modifier
                    .padding(top = paddingXSmall)
                    .fillMaxWidth()
                    .testTag(InputFieldErrorTag),
                errorMessage = errorMessage,
                textColor = errorMessageColor
            )
        }
    }
}

@Composable
@Preview(device = Devices.DEFAULT, showSystemUi = true)
private fun PreviewUserNameEmailView() {

    RumbleInputFieldView(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingMedium),
        label = "EMAIL OR USERNAME",
        initialValue = "The Dan Bongino Show",
        hasError = true,
        errorMessage = "Some error message"
    )
}