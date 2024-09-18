package com.rumble.battles.search.presentation.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.theme.paddingMedium

@Composable
fun SearchResultHeader(
    modifier: Modifier = Modifier,
    query: String,
    onBack: () -> Unit,
    onSearch: (String) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = paddingMedium, end = paddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                Icons.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.back)
            )
        }

        SearchQueryView(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSearch(query) },
            query = query
        )
    }
}