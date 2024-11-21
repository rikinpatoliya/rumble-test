package com.rumble.battles.commonViews

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.theme.RumbleTypography
import com.rumble.theme.fierceRed
import com.rumble.theme.imageMedium
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXSmall
import com.rumble.theme.radiusSmall

@Composable
fun RumbleTextInputField(
    initialValue: String,
    label: String,
    maxLines: Int,
    maxCharacters: Int,
    onValueChange: (String) -> Unit = {},
    hasError: Boolean = false,
    errorMessage: String = "",
    errorMessageColor: Color = MaterialTheme.colors.secondary,
) {
    var text by remember { mutableStateOf(initialValue) }
    val defaultCharactersCountText = ""
    val defaultCharactersCountTextColor = MaterialTheme.colors.primaryVariant
    var characters by remember { mutableStateOf(defaultCharactersCountText) }
    var charactersColor by remember { mutableStateOf(defaultCharactersCountTextColor) }

    Column(
        modifier = Modifier
            .padding(
                start = paddingMedium,
                end = paddingMedium
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = paddingMedium,
                ),
        ) {
            Text(
                text = label.uppercase(),
                modifier = Modifier.align(Alignment.CenterStart),
                color = MaterialTheme.colors.primary,
                style = RumbleTypography.h6Heavy,
            )
            Text(
                text = characters,
                modifier = Modifier.align(Alignment.CenterEnd),
                color = charactersColor,
                style = RumbleTypography.tinyBody
            )
        }
        OutlinedTextField(
            value = text,
            modifier = Modifier
                .fillMaxWidth(),
            textStyle = RumbleTypography.body1,
            shape = RoundedCornerShape(radiusSmall),
            onValueChange = {
                onValueChange(it)
                text = it
                characters = getCharactersText(it, maxCharacters, defaultCharactersCountText)
                charactersColor =
                    getCharactersTextColor(it, maxCharacters, defaultCharactersCountTextColor)
            },
            colors = TextFieldDefaults.textFieldColors(
                textColor = MaterialTheme.colors.primary,
                backgroundColor = MaterialTheme.colors.onSurface,
                cursorColor = MaterialTheme.colors.primary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = fierceRed
            ),
            isError = hasError,
            trailingIcon = {
                if (text.isNotBlank()) {
                    IconButton(
                        onClick = {
                            onValueChange("")
                            text = ""
                            characters =
                                getCharactersText("", maxCharacters, defaultCharactersCountText)
                            charactersColor =
                                getCharactersTextColor(
                                    "",
                                    maxCharacters,
                                    defaultCharactersCountTextColor
                                )
                        },
                        modifier = Modifier.size(imageMedium)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_clear_text),
                            contentDescription = stringResource(id = R.string.clear_text),
                            tint = MaterialTheme.colors.secondary
                        )
                    }
                }
            },
            maxLines = maxLines,
            minLines = maxLines,
        )
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

private fun getCharactersText(text: String, maxCharacters: Int, default: String): String {
    return when {
        text.isEmpty() -> default
        else -> "${text.count()}/${maxCharacters}"
    }
}

private fun getCharactersTextColor(text: String, maxCharacters: Int, default: Color): Color {
    return when {
        text.count() > maxCharacters -> fierceRed
        else -> default
    }
}