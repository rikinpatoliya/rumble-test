package com.rumble.battles.search.presentation.views

import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.battles.SearchQueryClearTextButtonTag
import com.rumble.theme.RumbleTypography
import com.rumble.theme.radiusXLarge
import com.rumble.utils.RumbleConstants

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchView(
    modifier: Modifier = Modifier,
    initialQuery: String = "",
    onSearch: (String) -> Unit = {},
    onQueryChanged: (String) -> Unit = {}
) {
    var query by remember {
        val initialText = initialQuery.take(RumbleConstants.SEARCH_INITIAL_MAX_LENGTH)
        val selection = TextRange(initialText.length)
        val textFieldValue = TextFieldValue(text = initialText, selection = selection)
        mutableStateOf(textFieldValue)
    }

    val focusRequester = FocusRequester()
    val keyboardController = LocalSoftwareKeyboardController.current

    SideEffect {
        focusRequester.requestFocus()
    }

    OutlinedTextField(
        modifier = modifier
//            .semantics { contentDescription = SearchQuerySearchBarTag }
            .focusRequester(focusRequester),
        shape = RoundedCornerShape(radiusXLarge),
        value = query,
        placeholder = {
            Text(stringResource(id = R.string.search_rumble))
        },
        onValueChange = {
            query = it
            onQueryChanged(query.text)
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.onSecondary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            placeholderColor = MaterialTheme.colors.primaryVariant
        ),
        textStyle = RumbleTypography.h5,
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            autoCorrect = false,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(onSearch = {
            keyboardController?.hide()
            if (query.text.isNotBlank()) onSearch(query.text)
        }),
        trailingIcon = {
            IconButton(
                modifier = Modifier.semantics {
                    contentDescription = SearchQueryClearTextButtonTag
                },
                onClick = {
                    query = TextFieldValue(text = "")
                    onQueryChanged(query.text)
                }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_clear_text),
                    contentDescription = stringResource(id = R.string.clear_search),
                    tint = MaterialTheme.colors.secondary
                )
            }
        }
    )
}

@Composable
@Preview
private fun Preview() {
    SearchView(Modifier.fillMaxWidth())
}