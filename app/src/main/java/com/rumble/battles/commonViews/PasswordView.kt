package com.rumble.battles.commonViews

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.LoginPasswordErrorTag
import com.rumble.battles.R
import com.rumble.theme.RumbleTypography
import com.rumble.theme.fierceRed
import com.rumble.theme.imageMedium
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusSmall

@Composable
fun PasswordView(
    modifier: Modifier = Modifier,
    testTag: String = "",
    label: String = "",
    labelColor: Color = MaterialTheme.colors.onPrimary,
    backgroundColor: Color = MaterialTheme.colors.onSurface,
    textColor: Color = MaterialTheme.colors.primary,
    cursorColor: Color = MaterialTheme.colors.primary,
    iconTintColor: Color = MaterialTheme.colors.secondary,
    errorMessageColor: Color = MaterialTheme.colors.secondaryVariant,
    initialValue: String = "",
    onValueChange: (String) -> Unit = {},
    hasError: Boolean = false,
    errorMessage: String = "",
) {

    var password by remember { mutableStateOf(initialValue) }
    var showPassword by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(bottom = paddingXXXSmall),
            style = RumbleTypography.h6Heavy,
            text = label,
            color = labelColor
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag),
            shape = RoundedCornerShape(radiusSmall),
            value = password,
            onValueChange = {
                onValueChange(it)
                password = it
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = backgroundColor,
                textColor = textColor,
                cursorColor = cursorColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = fierceRed
            ),
            textStyle = RumbleTypography.body1,
            singleLine = true,
            isError = hasError,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (password.isNotBlank()) {
                        IconButton(
                            onClick = {
                                password = ""
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
                    val icon =
                        if (showPassword) painterResource(id = R.drawable.ic_visible)
                        else painterResource(id = R.drawable.ic_visible_off)
                    IconButton(
                        onClick = {
                            showPassword = showPassword.not()
                        },
                        modifier = Modifier.size(imageMedium)
                    ) {
                        Icon(
                            painter = icon,
                            contentDescription = stringResource(id = R.string.change_password_visibility),
                            tint = iconTintColor
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            )
        )

        if (hasError) {
            ErrorMessageView(
                modifier = Modifier
                    .testTag(LoginPasswordErrorTag)
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
private fun PreviewPasswordView() {

    PasswordView(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingMedium),
        label = "PASSWORD",
        initialValue = "Password",
        hasError = true,
        errorMessage = "Some error message"
    )
}