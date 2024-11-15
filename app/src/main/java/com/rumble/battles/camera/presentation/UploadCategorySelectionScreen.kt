package com.rumble.battles.camera.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rumble.battles.R
import com.rumble.battles.UploadCategorySelectionTag
import com.rumble.battles.commonViews.RumbleBasicTopAppBar
import com.rumble.domain.discover.domain.domainmodel.CategoryEntity
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTypography
import com.rumble.theme.borderXSmall
import com.rumble.theme.borderXXSmall
import com.rumble.theme.imageXMedium
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusMedium
import com.rumble.theme.radiusXLarge
import com.rumble.theme.radiusXXSmall
import com.rumble.theme.rumbleGreen
import com.rumble.utils.RumbleConstants

@Composable
fun UploadCategorySelectionScreen(
    cameraUploadHandler: CameraUploadHandler,
    isPrimary: Boolean,
    onBackClick: () -> Unit,
) {
    val uiState by cameraUploadHandler.uiState.collectAsStateWithLifecycle()
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val categories by remember(searchQuery) {
        mutableStateOf(
            if (isPrimary) {
                uiState.primaryCategories.filter {
                    it.title.contains(searchQuery, ignoreCase = true)
                }
            } else {
                uiState.secondaryCategories.filter {
                    it.title.contains(searchQuery, ignoreCase = true)
                }
            }
        )
    }
    val selectedCategoryId = if (isPrimary) {
        uiState.selectedPrimaryCategory?.id
    } else {
        uiState.selectedSecondaryCategory?.id
    }

    Column(
        modifier = Modifier
            .testTag(UploadCategorySelectionTag)
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .imePadding()
    ) {
        RumbleBasicTopAppBar(
            title = stringResource(
                id = if (isPrimary) {
                    R.string.select_primary_category
                } else {
                    R.string.select_secondary_category
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding(),
            onBackClick = onBackClick,
        )

        SearchCategoryField(
            modifier = Modifier
                .padding(horizontal = paddingMedium, vertical = paddingXXXSmall)
                .fillMaxWidth(),
            searchQuery = searchQuery,
            onQueryChanged = { searchQuery = it }
        )

        LazyColumn(
            contentPadding = PaddingValues(bottom = paddingMedium),
            modifier = Modifier.padding(horizontal = paddingMedium, vertical = paddingXSmall),
            verticalArrangement = Arrangement.spacedBy(paddingXSmall)
        ) {
            items(categories) { category ->
                CategorySelectableRow(
                    category = category,
                    selected = category.id == selectedCategoryId,
                    onSelectCategory = {
                        if (isPrimary) {
                            cameraUploadHandler.onPrimaryCategorySelected(it)
                        } else {
                            cameraUploadHandler.onSecondaryCategorySelected(it)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun CategorySelectableRow(
    category: CategoryEntity,
    selected: Boolean,
    onSelectCategory: (CategoryEntity) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(radiusMedium))
            .border(
                shape = RoundedCornerShape(radiusMedium),
                width = if (selected) borderXSmall else borderXXSmall,
                color = if (selected) rumbleGreen else RumbleCustomTheme.colors.backgroundHighlight
            )
            .selectable(
                selected = selected,
                onClick = { onSelectCategory(category) }
            )
            .padding(
                start = paddingSmall,
                end = paddingMedium,
                top = paddingMedium,
                bottom = paddingMedium
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(paddingSmall)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(category.thumbnail)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .size(imageXMedium)
                .clip(RoundedCornerShape(radiusXXSmall)),
            contentScale = ContentScale.Crop
        )

        Text(
            text = category.title,
            color = MaterialTheme.colors.primary,
            style = RumbleTypography.h4,
            modifier = Modifier.weight(1f)
        )

        if (selected) {
            Icon(
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = null,
                tint = rumbleGreen
            )
        }
    }
}

@Composable
private fun SearchCategoryField(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onQueryChanged: (String) -> Unit
) {
    var query by remember {
        val initialText = searchQuery.take(RumbleConstants.SEARCH_INITIAL_MAX_LENGTH)
        val selection = TextRange(initialText.length)
        val textFieldValue = TextFieldValue(text = initialText, selection = selection)
        mutableStateOf(textFieldValue)
    }

    OutlinedTextField(
        modifier = modifier,
        shape = RoundedCornerShape(radiusXLarge),
        value = query,
        placeholder = { Text(stringResource(id = R.string.search_categories)) },
        onValueChange = {
            query = it
            onQueryChanged(query.text)
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = RumbleCustomTheme.colors.subtleHighlight,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            placeholderColor = MaterialTheme.colors.primary.copy(alpha = 0.5f),
            textColor = MaterialTheme.colors.primary
        ),
        textStyle = RumbleTypography.h5,
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            autoCorrect = false,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        trailingIcon = {
            IconButton(
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