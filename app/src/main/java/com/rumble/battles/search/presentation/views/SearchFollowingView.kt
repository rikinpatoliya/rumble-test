package com.rumble.battles.search.presentation.views

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography
import com.rumble.theme.radiusSmall

@Composable
fun SearchFollowingView(
    modifier: Modifier = Modifier,
    query: String = "",
    onQueryChanged: (String) -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(radiusSmall),
        value = query,
        placeholder = {
            Text(stringResource(id = R.string.search_following))
        },
        onValueChange = {
            onQueryChanged(it)
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.onSurface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            placeholderColor = MaterialTheme.colors.primaryVariant
        ),
        textStyle = RumbleTypography.body1,
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            autoCorrect = false,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(onSearch = {
            keyboardController?.hide()
        }),
    )
}

@Composable
@Preview
private fun Preview() {
    RumbleTheme {
        SearchFollowingView()
    }
}